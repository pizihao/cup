package com.qlu.cup.executor;

import com.qlu.cup.context.Environment;
import com.qlu.cup.logging.Log;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.result.ResultProcessor;
import com.qlu.cup.session.RowBounds;
import com.qlu.cup.statement.SimpleStatementHandler;
import com.qlu.cup.statement.StatementHandler;
import com.qlu.cup.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

/**
 * 简单执行器
 */
public class CupExecutor extends BaseExecutor {

    public CupExecutor(Environment environment, Transaction transaction) {
        super(environment, transaction);
    }

    //update
    @Override
    public int doUpdate(BoundSql boundSql, Object parameter) throws SQLException {
        Statement stmt = null;
        try {
            //新建一个StatementHandler
            //这里看到ResultHandler传入的是null
            StatementHandler handler = new SimpleStatementHandler(this, parameter, RowBounds.DEFAULT, null, null);
            //准备语句
            stmt = prepareStatement(handler, boundSql.getStatementLog());
            //StatementHandler.update
            return handler.update(stmt);
        } finally {
            closeStatement(stmt);
        }
    }

    //select
    @Override
    public <E> List<E> doQuery(Object parameter, RowBounds rowBounds, ResultProcessor resultHandler, BoundSql boundSql) throws SQLException {
        Statement stmt = null;
        try {
            //新建一个StatementHandler
            //这里看到ResultHandler传入了
            StatementHandler handler = new SimpleStatementHandler(wrapper, parameter, rowBounds, resultHandler, boundSql);
            //准备语句
            stmt = prepareStatement(handler, boundSql.getStatementLog());
            //StatementHandler.query
            return handler.<E>query(stmt, resultHandler);
        } finally {
            closeStatement(stmt);
        }
    }

    private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
        Statement stmt;
        Connection connection = getConnection(statementLog);
        //调用StatementHandler.prepare
        stmt = handler.prepare(connection);
        //调用StatementHandler.parameterize
        handler.parameterize(stmt);
        return stmt;
    }

}
