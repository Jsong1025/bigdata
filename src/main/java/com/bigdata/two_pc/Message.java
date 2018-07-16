package com.bigdata.two_pc;

import java.io.Serializable;
import java.util.Vector;

public class Message implements Serializable{

    private LocalNode sendNode;

    private String key;

    private TransactionMsg msg;

    public Message() {
    }

    public Message(Vector vector) {
        String address = (String) vector.get(0);
        Integer port = (Integer) vector.get(1);
        String key = (String) vector.get(2);
        String msg = (String) vector.get(3);
        this.sendNode = new Participant(address, port);
        this.key = key;
        this.msg = TransactionMsg.getTransactionMsg(msg);
    }

    public Message(LocalNode sendNode, String key, TransactionMsg msg) {
        this.sendNode = sendNode;
        this.key = key;
        this.msg = msg;
    }

    public LocalNode getSendNode() {
        return sendNode;
    }

    public void setSendNode(LocalNode sendNode) {
        this.sendNode = sendNode;
    }

    public TransactionMsg getMsg() {
        return msg;
    }

    public void setMsg(TransactionMsg msg) {
        this.msg = msg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Vector getVector() {
        Vector vector = new Vector();
        vector.addElement(this.sendNode.getAddress());
        vector.addElement(this.sendNode.getPort());
        vector.addElement(this.key);
        vector.addElement(this.msg.getValue());
        return vector;
    }
}
