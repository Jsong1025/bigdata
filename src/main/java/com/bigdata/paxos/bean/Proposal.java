package com.bigdata.paxos.bean;

public class Proposal {

    private Integer id;
    private Object value;

    public Proposal() {
    }

    public Proposal(Integer id, Object value) {
        this.id = id;
        this.value = value;
    }

    public Proposal(Proposal proposal) {
        this.id = proposal.id;
        this.value = proposal.value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void newId(Integer minId, Integer acceptorNum) {
        this.id = minId * acceptorNum + this.id;
    }
}
