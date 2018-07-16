package com.bigdata.two_pc;

import java.util.ArrayList;
import java.util.List;

public class TransactionManager {

    private Coordinator coordinator;
    private List<String> sqls = new ArrayList<>();

    public TransactionManager(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    /**
     * 事务管理器的执行SQL只是将SQL缓存
     */
    public void execute(String sql) {
        sqls.add(sql);
    }

    public void commit() throws Exception {
        StringBuffer sb = new StringBuffer();
        for (String sql : sqls) {
            sb.append(sql).append(";");
        }
        String sql = sb.toString();
        // SQL拼接与时间戳做hash 防止hash相同
        String key = SHA1Utils.hash(sql + System.currentTimeMillis());
        coordinator.execute(key, sql);
        sqls.clear();

        Message message = new Message();
        message.setKey(key);
        coordinator.commit(message);
    }
}
