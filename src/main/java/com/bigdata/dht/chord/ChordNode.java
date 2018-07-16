package com.bigdata.dht.chord;

import java.util.*;

public class ChordNode {

    private ChordDataKey nodeId;
    private String chordKey;
    private ChordNode successor = null;
    private ChordNode predecessor = null;
    private Map<Integer, ChordNode> fingerTable = new TreeMap<>();

    private Map<ChordDataKey, ChordDataValue> data = new HashMap<>();

    public Object getNodeId() {
        return nodeId;
    }

    public void setNodeId(ChordDataKey nodeId) {
        this.nodeId = nodeId;
        this.chordKey = nodeId.sha1Hash();
    }

    public String getChordKey() {
        return chordKey;
    }

    public ChordNode getSuccessor() {
        return successor;
    }

    public void setSuccessor(ChordNode successor) {
        this.successor = successor;
    }

    public ChordNode getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(ChordNode predecessor) {
        this.predecessor = predecessor;
    }

    public Map<Integer, ChordNode> getFingerTable() {
        return fingerTable;
    }

    public void setFingerTable(Map<Integer, ChordNode> fingerTable) {
        this.fingerTable = fingerTable;
    }

    public void addData(Object key, Object value) {

    }

    public Map<ChordDataKey, ChordDataValue> getData() {
        return data;
    }

    public void setData(Map<ChordDataKey, ChordDataValue> data) {
        this.data = data;
    }

    public void addNode(ChordNode node) {
        ChordNode successor = this.findSuccessor(node.getChordKey());
        ChordNode predecessor = successor.getPredecessor();
        node.setSuccessor(successor);
        stabilization(node);
        stabilization(successor);
        stabilization(predecessor);
    }

    public Object getValue(ChordDataKey key) {
        String hash = key.sha1Hash();
        if (this.chordKey.compareToIgnoreCase(hash) < 0 && this.successor.chordKey.compareToIgnoreCase(hash) >= 0) {
            return this.successor.data.get(key);
        }
        else {
            ChordNode node = this.findSuccessor(hash);
            if (node == null) {
                return null;
            } else {
                return node.getValue(key);
            }
        }
    }

    public ChordNode findSuccessor(String hashKey) {
        for (Map.Entry<Integer, ChordNode> entry : fingerTable.entrySet()) {
            String key = HexUtils.add(this.chordKey, entry.getKey());
            if (HexUtils.compareTo(hashKey, key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void stabilization(ChordNode node) {
        ChordNode successor = node.getSuccessor();
        ChordNode predecessor = successor.getPredecessor();
        if (predecessor.getNodeId() != node.getNodeId()) { // 检查后继节点的前驱节点是否是自身
            if (successor.getChordKey().compareToIgnoreCase(node.getChordKey()) > 0 &&
                    predecessor.getChordKey().compareToIgnoreCase(node.getChordKey()) < 0) {
                node.setSuccessor(predecessor);
            }
            if (node.getSuccessor().getPredecessor() == null ||
                    (node.getSuccessor().getChordKey().compareToIgnoreCase(node.getChordKey()) > 0 &&
                            node.getSuccessor().getPredecessor().getChordKey().compareToIgnoreCase(node.getChordKey()) < 0)) {
                node.getSuccessor().setPredecessor(node);
            }
        }
        moveData(successor, node);
    }



    // 重新分配数据
    private void moveData(ChordNode fromNode, ChordNode toNode) {
        List<ChordDataKey> removeKeys = new ArrayList<>();
        for (Map.Entry<ChordDataKey, ChordDataValue> entry : fromNode.getData().entrySet()) {
            String key = entry.getKey().sha1Hash();
            if (key.compareToIgnoreCase(toNode.getChordKey()) < 0) { // 标记下所有小于toNode key的数据
                removeKeys.add(entry.getKey());
            }
        }

        for (ChordDataKey key : removeKeys) {
            toNode.getData().put(key, fromNode.getData().get(key));
            fromNode.getData().remove(key);
        }
    }

}
