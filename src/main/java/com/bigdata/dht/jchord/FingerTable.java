package com.bigdata.dht.jchord;

import java.math.*;

/**
 * 路由表
 */
public class FingerTable {

    private RemoteNode finger[];

    private BigInteger fingerStart[];

    private ChordApp callback;

    public FingerTable(LocalNode parent) {
        callback = parent.getCallback();
        finger = new RemoteNode[callback.IDENTIFIER_BITS + 1];
        fingerStart = new BigInteger[callback.IDENTIFIER_BITS + 1];

        NodeIdentity nodeID = parent.getIdentifier();

        BigInteger base = new BigInteger("2");
        base = base.pow(callback.IDENTIFIER_BITS);

        BigInteger nodeNum = nodeID.getAsBigInt();

        for (int i = 1; i < finger.length; i++) {
            finger[i] = null;
            fingerStart[i] = makeStart(nodeNum, base, i);
//      finger[i] = new Finger();
//      finger[i].start = makeStart(nodeNum, base, i);

//      if (i != 1) 
//        finger[i-1].intervalEnd = finger[i].start;
//      if (i == Simulator.IDENTIFIER_BITS)  
//        finger[i].intervalEnd = nodeNum;
        }
    }

    private BigInteger makeStart(BigInteger n, BigInteger base, int i) {
        BigInteger out = new BigInteger("2");

        out = out.pow(i - 1);
        out = out.add(n);
        out = out.mod(base);

        return out;
    }

    public void updateFinger(int i, RemoteNode n) {
        finger[i] = n;
//    finger[i].node = n;
    }

    public RemoteNode getFinger(int i) {
        return finger[i];
//    return finger[i].node;
    }

    public BigInteger getFingerStart(int i) {
        return fingerStart[i];
//    return finger[i].start;
    }
}
