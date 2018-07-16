package com.bigdata.two_pc;

// 节点状态
public enum TransactionStatus {

    INIT, READY, WAIT, ABORT, COMMIT;

}
