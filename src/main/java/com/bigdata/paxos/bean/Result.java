package com.bigdata.paxos.bean;

public class Result {

    private boolean isPromised;

    private Proposal proposal;

    public Result() {
    }

    public Result(boolean isPromised, Proposal proposal) {
        this.isPromised = isPromised;
        this.proposal = proposal;
    }

    public boolean isPromised() {
        return isPromised;
    }

    public void setPromised(boolean promised) {
        isPromised = promised;
    }

    public Proposal getProposal() {
        return proposal;
    }

    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    public static Result accept(Proposal proposal) {
        return new Result(true, proposal);
    }

    public static Result reject(Proposal proposal) {
        return new Result(false, proposal);
    }
}
