package com.bigdata.two_pc;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

public class Participant extends LocalNode {

    public Participant(String address, Integer port) {
        super(address, port);
    }

    public FutureTask<Message> sendVoting(final Message message, final CountDownLatch latch) {
        final String url = "http://" + super.getAddress() +":"+ super.getPort();

        return new FutureTask<Message>(new Callable<Message>() {
            @Override
            public Message call() throws Exception {
                XmlRpcClient client = new XmlRpcClient(url);
                Vector param = new Vector();
                param.addElement(message.getVector());
                Vector result = (Vector) client.execute("node.voting", param);
                return new Message(result);
            }
        }) {
            @Override
            protected void done() {
                super.done();
                latch.countDown();
            }
        };
    }

    public FutureTask<Message> sendCommit(final Message message, final CountDownLatch latch) {
        final String url = "http://" + super.getAddress() +":"+ super.getPort();

        return new FutureTask<Message>(new Callable<Message>() {
            @Override
            public Message call() throws Exception {
                XmlRpcClient client = new XmlRpcClient(url);
                Vector param = new Vector();
                param.addElement(message.getVector());
                Vector result = (Vector) client.execute("node.commit", param);
                return new Message(result);
            }
        }) {
            @Override
            protected void done() {
                super.done();
                latch.countDown();
            }
        };
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
        if (message.getMsg() == TransactionMsg.VOTE_REQUEST) {
            // do something
            System.out.println("Participant : " + getAddress() + ":" + getPort() + " : voting");
            transaction.setStatus(TransactionStatus.READY);
            return new Message(this, TransactionMsg.GLOBAL_COMMIT);
        }
        return new Message(this, TransactionMsg.VOTE_ABORT);
    }

    public Message commit(Message message) throws Exception{
        Transaction transaction = getTransaction(message.getKey());
        if (message.getMsg() == TransactionMsg.GLOBAL_COMMIT) {
            // do something
            System.out.println("Participant : " + getAddress() + ":" + getPort() + " : commit");
            transaction.setStatus(TransactionStatus.COMMIT);
            return new Message(this, TransactionMsg.GLOBAL_COMMIT);
        }
        transaction.setStatus(TransactionStatus.ABORT);
        return new Message(this, TransactionMsg.GLOBAL_ABORT);
    }

    @Override
    public boolean execute(String key, String sqls) {
        Transaction transaction = new Transaction(key, TransactionStatus.INIT);
        transactions.put(key, transaction);
        System.out.println("Participant : " + getAddress() + ":" + getPort() + " : execute " + sqls);
        return true;
    }

    public Runnable sendExecute(final String key, final String sqls, final CountDownLatch latch) {
        final String url = "http://" + super.getAddress() +":"+ super.getPort();

        return new Runnable() {
            @Override
            public void run() {
                try {
                    XmlRpcClient client = new XmlRpcClient(url);
                    Vector param = new Vector();
                    param.addElement(key);
                    param.addElement(sqls);
                    client.execute("node.execute", param);
                    latch.countDown();
                } catch (XmlRpcException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

        @Override
    public void start() {
        super.bind(this);
        super.start();
    }

    public static void main(String[] args) {
        Participant participant = new Participant("localhost", 8083);
        participant.start();
    }

}
