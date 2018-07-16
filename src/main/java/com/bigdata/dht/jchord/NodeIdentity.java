package com.bigdata.dht.jchord;

import java.math.*;
import java.util.*;

/**
 * 节点基本信息
 */
public class NodeIdentity {

    private BigInteger nodeId;

    private String address;

    private int port;

    private boolean search;

    public NodeIdentity(String ident, String address, int port) {
        this.address = address;
        this.port = port;
        this.search = false;

        SHA1 sha1 = new SHA1();
        sha1.init();
        sha1.updateASCII(ident);
        sha1.finish();
        this.nodeId = new BigInteger(1, sha1.digestBits);
    }

    public NodeIdentity(BigInteger ident, String address, int port) {
        this.address = address;
        this.nodeId = ident;
        this.port = port;
        this.search = false;
    }

    /**
     * 依据vector队列初始化，格式为(String)nodeid, (String)ip, (Integer)port
     */
    public NodeIdentity(Vector id) {
        this.nodeId = new BigInteger((String) id.elementAt(0), 16);
        this.address = (String) id.elementAt(1);
        this.port = (Integer) id.elementAt(2);
        this.search = false;
    }

    public NodeIdentity(String ident, boolean encoded) {
        if (!encoded) {
            SHA1 s = new SHA1();
            s.init();
            s.updateASCII(ident);
            s.finish();
            this.nodeId = new BigInteger(1, s.digestBits);
        }
        else {
            this.nodeId = new BigInteger(ident, 16);
        }
        search = true;
    }

    public String toString() {
        return nodeId.toString(16);
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public BigInteger getAsBigInt() {
        return nodeId;
    }

    public boolean equals(NodeIdentity identity) {
        return nodeId.compareTo(identity.getAsBigInt()) == 0;
    }

    /**
     * 判断某个节点是否在区间内
     */
    public static boolean inRange(BigInteger identity, BigInteger low, BigInteger high) {
        if (identity == null){
            return false;
        }

        // low = high, entire chord
        if (low.compareTo(high) == 0) return true;

        if (low.compareTo(high) > 0) {
            return (identity.compareTo(low) > 0) || (identity.compareTo(high) < 0);
        } else {
            return (identity.compareTo(low) > 0) && (identity.compareTo(high) < 0);
        }
    }

    public static boolean inRange(NodeIdentity identity, NodeIdentity low, NodeIdentity high) {
        return identity != null && inRange(identity.getAsBigInt(), low.getAsBigInt(), high.getAsBigInt());
    }

    public static boolean inRange(RemoteNode remoteNode, RemoteNode low, RemoteNode high) {
        return remoteNode != null && inRange(remoteNode.getIdentifier(), low.getIdentifier(), high.getIdentifier());
    }
}

