package com.bigdata.two_pc;

import java.util.ArrayList;
import java.util.List;

public class TransactionManager {

    private Coordinator coordinator;
    private List<String> sqls = new ArrayList<>();

    public TransactionManager(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public void execute(String sql) {
        sqls.add(sql);
    }

    public void commit() throws Exception {
        StringBuffer sb = new StringBuffer();
        for (String sql : sqls) {
            sb.append(sql).append(";");
        }
        String sql = sb.toString();
        String key = SHA1Utils.hash(sql);
        coordinator.execute(key, sql);
        sqls.clear();

        Message message = new Message();
        message.setKey(key);
        coordinator.commit(message);
    }
}
