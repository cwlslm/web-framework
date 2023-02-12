package com.jfeng.framework.transaction;

import com.jfeng.framework.dao.DatabaseHelper;

/**
 * 事务管理器
 */
public class TransactionManager {
    /**
     * 开启事务
     */
    public static int begin() {
        return DatabaseHelper.beginTransaction();
    }

    /**
     * 提交事务（指定ID）
     */
    public static void commit(int id) {
        DatabaseHelper.commitTransaction(id);
    }

    /**
     * 提交事务（列表最后一个）
     */
    public static void commit() {
        DatabaseHelper.commitTransaction();
    }

    /**
     * 回滚事务（指定ID）
     */
    public static void rollback(int id) {
        DatabaseHelper.rollbackTransaction(id);
    }

    /**
     * 回滚事务（列表最后一个）
     */
    public static void rollback() {
        DatabaseHelper.rollbackTransaction();
    }
}
