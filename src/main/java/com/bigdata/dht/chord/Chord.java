package com.bigdata.dht.chord;

import java.util.ArrayList;
import java.util.List;

public class Chord {

    private List<ChordNode> nodes = new ArrayList<>();

    public Chord() {
    }


    public void addNode(ChordNode node) {
        String newChordKey = node.getChordKey();

        // 节点不为空
        if (nodes.size() != 0) {
            ChordNode afterNode = nodes.get(0).findSuccessor(newChordKey);
            node.setSuccessor(afterNode);
        }
        else {
            node.setPredecessor(node);
            node.setSuccessor(node);
            nodes.add(node);
        }
    }


}
