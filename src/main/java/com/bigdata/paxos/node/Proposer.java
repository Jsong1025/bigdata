package com.bigdata.paxos.node;

import com.bigdata.paxos.bean.Proposal;
import com.bigdata.paxos.bean.Result;

import java.util.ArrayList;
import java.util.List;

public class Proposer implements Runnable  {

    private Proposal proposal;

    private List<Acceptor> acceptors = new ArrayList<>();

    void prepare() {
        for (Acceptor acceptor: acceptors) {
            Result result = Result.reject(proposal);

            Integer maxId = 0;
            while (result.isPromised()) {
                Proposal proposal = new Proposal(this.proposal);
                proposal.newId(maxId, acceptors.size());
                result = acceptor.prapare(proposal);
                maxId = result.getProposal().getId();
            }
        }
    }

    void accept() {
        for (Acceptor acceptor: acceptors) {
            Result result = acceptor.accept(proposal);
        }
    }

    @Override
    public void run() {

    }
}
