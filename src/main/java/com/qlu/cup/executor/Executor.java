package com.qlu.cup.executor;

import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.transaction.Transaction;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

/**
 * @program: cup
 * @description: 执行器
 * @author: liuwenhao
 * @create: 2021-01-25 22:23
 **/
public interface Executor {

    /**
     * 更新
     */
    int update(BoundSql boundSql, Object parameter) throws SQLException;

    /**
     * 查询，带分页
     */
    <E> List<E> query(BoundSql boundSql, Object parameter) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException;

    /**
     * 提交，参数是是否要强制
     */
    void commit(boolean required) throws SQLException;

    /**
     * 回滚，参数是是否要强制
     */
    void rollback(boolean required) throws SQLException;

    Transaction getTransaction();

    void close(boolean forceRollback);

    boolean isClosed();

    void setExecutorWrapper(Executor executor);
}