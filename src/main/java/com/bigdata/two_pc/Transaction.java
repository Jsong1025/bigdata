package com.bigdata.two_pc;

public class Transaction {

    private String id;
    private TransactionStatus status;

    public Transaction() {
    }

    public Transaction(String id, TransactionStatus status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
