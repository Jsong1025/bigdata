package com.bigdata.two_pc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Coordinator extends LocalNode {

    List<Participant> participants = new ArrayList<>();
    ExecutorService executor = Executors.newCachedThreadPool();

    public Coordinator(String address, Integer port) {
        super(address, port);
//        participants.add(new Participant("localhost", 8081));
//        participants.add(new Participant("localhost", 8082));
        participants.add(new Participant("localhost", 8083));
    }


    /**
     * Coordinator节点确认动作
     */
    public Message voting(Message message) throws Exception {
        Transaction transaction = getTransaction(message.getKey());
        while (transaction.getStatus().compareTo(TransactionStatus.INIT) != 0) {
            try {
                Thread.sleep(500l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        message.setMsg(TransactionMsg.VOTE_REQUEST);
        message.setSendNode(this);

        List<FutureTask<Message>> tasks = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(participants.size());

        List<Message> resultMessage = new ArrayList<>();
        for (Participant participant : participants) {
            FutureTask task = participant.sendVoting(message, latch);
            executor.execute(task);
            tasks.add(task);
        }


        for (FutureTask<Message> task : tasks) {
            try {
                Message msg = task.get();
                resultMessage.add(msg);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                resultMessage.add(new Message(null, message.getKey(), TransactionMsg.VOTE_ABORT));
            }
        }

        try {
            transaction.setStatus(TransactionStatus.WAIT);
            latch.await();
            return getTransaction(message.getKey(), resultMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Message(this, message.getKey(), TransactionMsg.GLOBAL_ABORT);
        }
    }

    private Message getTransaction(String key, List<Message> resultMessage) {
        TransactionMsg msg = TransactionMsg.GLOBAL_ABORT;
        for (Message tran : resultMessage) {
            if (tran.getMsg() == TransactionMsg.VOTE_ABORT) {
                return new Message(this, key, msg);
            }
        }

        return new Message(this, key, TransactionMsg.GLOBAL_COMMIT);
    }

    /**
     * Coordinator节点 提交动作
     */
    public Message commit(Message message) throws Exception {
        System.out.println("Coordinator : " + getAddress() + ":" + getPort() + " : start voting");
        message = voting(message);
        System.out.println("Coordinator : " + getAddress() + ":" + getPort() + " : voting end");

        System.out.println("Coordinator : " + getAddress() + ":" + getPort() + " : start commit");
        List<FutureTask<Message>> tasks = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(participants.size());
        for (Participant participant : participants) {
            FutureTask task = participant.sendCommit(message, latch);
            executor.execute(task);
            tasks.add(task);
        }

        for (FutureTask<Message> task : tasks) {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        try {
            latch.await();
            Transaction transaction = getTransaction(message.getKey());
            if (message.getMsg() == TransactionMsg.GLOBAL_ABORT) {
                transaction.setStatus(TransactionStatus.ABORT);
            }
            else {
                transaction.setStatus(TransactionStatus.COMMIT);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Coordinator : " + getAddress() + ":" + getPort() + " : commit end");
        return message;
    }

    /**
     * 开启事务接口，返回一个事务管理器
     */
    public TransactionManager startTransaction() {
        return new TransactionManager(this);
    }

    /**
     * 执行SQL
     */
    @Override
    public boolean execute(String key, String sqls) {
        if (key == null) {
            return false;
        }

        System.out.println("Coordinator : " + getAddress() + ":" + getPort() + " : execute " + sqls);

        Transaction transaction = new Transaction(key, TransactionStatus.INIT);
        transactions.put(key, transaction);

        CountDownLatch latch = new CountDownLatch(participants.size());

        for (Participant participant : participants) {
            executor.submit(participant.sendExecute(key, sqls, latch));
        }

        try {
            latch.await();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        Coordinator coordinator = new Coordinator("localhost", 8080);
        TransactionManager manager = coordinator.startTransaction();
        manager.execute("insert table");
        manager.commit();
    }

}
