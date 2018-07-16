package com.bigdata.two_pc;

public enum TransactionMsg {

    VOTE_REQUEST("VOTE_REQUEST"),
    VOTE_COMMIT("VOTE_COMMIT"),
    VOTE_ABORT("VOTE_ABORT"),

    GLOBAL_COMMIT("GLOBAL_COMMIT"),
    GLOBAL_ABORT("GLOBAL_ABORT");

    TransactionMsg(String value) {
        this.value = value;
    }

    public static TransactionMsg getTransactionMsg(String msg) {
        return TransactionMsg.valueOf(msg);
    }

    private String value;

    public String getValue() {
        return value;
    }
}
