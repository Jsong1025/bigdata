package com.bigdata.two_pc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


    public Message voting(Message message) throws Exception{
        Transaction transaction = getTransaction(message.getKey());
        while (transaction.getStatus().compareTo(TransactionStatus.INIT) == 0) {
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
        for (Participant participant: participants) {
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
            }
        }

        try {
            transaction.setStatus(TransactionStatus.WAIT);
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getTransaction(resultMessage);
    }

    private Message getTransaction(List<Message> resultMessage) {
        TransactionMsg msg = TransactionMsg.GLOBAL_ABORT;
        for (Message tran: resultMessage) {
            if (tran.getMsg() == TransactionMsg.VOTE_ABORT) {
                return new Message(this, msg);
            }
        }

        return new Message(this, TransactionMsg.GLOBAL_COMMIT);
    }

    public Message commit(Message message) throws Exception{
        System.out.println("Coordinator : " + getAddress() + ":" + getPort() + " : start voting");
        message = voting(message);
        System.out.println("Coordinator : " + getAddress() + ":" + getPort() + " : voting end");

        System.out.println("Coordinator : " + getAddress() + ":" + getPort() + " : start commit");
        List<FutureTask<Message>> tasks = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(participants.size());
        for (Participant participant: participants) {
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

    public TransactionManager startTransaction() {
        return new TransactionManager(this);
    }

    @Override
    public boolean execute(String key, String sqls) {
        if (key == null) {
            key = SHA1Utils.hash(sqls);
        }
        System.out.println("Coordinator : " + getAddress() + ":" + getPort() + " : execute " + sqls);

        Transaction transaction = new Transaction(key, TransactionStatus.INIT);
        transactions.put(key, transaction);

        CountDownLatch latch = new CountDownLatch(participants.size());

        for (Participant participant: participants) {
            executor.submit(participant.sendExecute(key, sqls, latch));
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        Coordinator coordinator = new Coordinator("localhost", 8080);
        TransactionManager manager = coordinator.startTransaction();
        manager.execute("insert table");
        manager.commit();
    }

}
