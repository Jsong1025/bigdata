package com.bigdata.two_pc;

import org.apache.xmlrpc.WebServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LocalNode {

    protected Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    private String address;
    private Integer port;

    private WebServer rpcServer;

    public LocalNode(String address, Integer port) {
        this.address = address;
        this.port = port;
    }

    public void bind(LocalNode node) {
        try {
            rpcServer = new WebServer(this.getPort());
            rpcServer.addHandler("node", this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        rpcServer.start();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    abstract Message voting(Message message) throws Exception;

    public Vector voting(Vector vector) throws Exception{
        Message message = new Message(vector);
        return voting(message).getVector();
    }

    abstract Message commit(Message message) throws Exception;

    public Vector commit(Vector vector) throws Exception{
        Message message = new Message(vector);
        return commit(message).getVector();
    }

    public boolean execute(Vector vector) {
        String key = (String) vector.get(0);
        String sql = (String) vector.get(1);
        return this.execute(key, sql);
    }

    abstract boolean execute(String key, String sql);

    protected Transaction getTransaction(String key) throws Exception {
        Transaction transaction = transactions.get(key);
        if (transaction == null) {
            throw new Exception("do not found transaction : " + key);
        }
        return transaction;
    }
}
