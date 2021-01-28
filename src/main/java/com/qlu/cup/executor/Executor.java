package com.qlu.cup.executor;

import com.qlu.cup.builder.yml.YNode;
import com.qlu.cup.result.ResultProcessor;
import com.qlu.cup.session.RowBounds;
import com.qlu.cup.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * @program: cup
 * @description: 执行器
 * @author: liuwenhao
 * @create: 2021-01-25 22:23
 **/
public interface Executor {

    //不需要ResultProcessor
    ResultProcessor NO_RESULT_HANDLER = null;

    //更新
    int update(YNode ms, Object parameter) throws SQLException;

    //查询，带分页
    <E> List<E> query(YNode ms, Object parameter, RowBounds rowBounds, ResultProcessor ResultProcessor) throws SQLException;

    //提交和回滚，参数是是否要强制
    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    Transaction getTransaction();

    void close(boolean forceRollback);

    boolean isClosed();

    void setExecutorWrapper(Executor executor);
}