package com.bigdata.paxos.node;

import com.bigdata.paxos.bean.Proposal;
import com.bigdata.paxos.bean.Result;

public class Acceptor implements Runnable {

    private Object acceptValue;

    private Proposal mimProposal;

    public Result prapare(Proposal proposal) {
        if (mimProposal == null || mimProposal.getId() <= proposal.getId()) {
            mimProposal = proposal;
            return Result.accept(proposal);
        }
        return Result.reject(mimProposal);
    }


    public Result accept(Proposal proposal) {
        if (mimProposal.getId() <= proposal.getId()) {
            acceptValue = proposal.getValue();
            return Result.accept(proposal);
        }
        return Result.reject(mimProposal);
    }

    @Override
    public void run() {

    }
}
