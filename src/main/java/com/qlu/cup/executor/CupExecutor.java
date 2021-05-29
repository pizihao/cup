package com.qlu.cup.executor;

import com.qlu.cup.bind.Environment;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.mapper.SqlType;
import com.qlu.cup.statement.ParameterHandler;
import com.qlu.cup.statement.SimpleStatementHandler;
import com.qlu.cup.statement.StatementHandler;
import com.qlu.cup.transaction.Transaction;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
            StatementHandler handler = new SimpleStatementHandler(wrapper, parameter, boundSql);
            stmt = prepareStatement(handler, SqlType.valueOf(boundSql.getSqlType()).getParameterHandler());
            return handler.update(stmt);
        } finally {
            closeStatement(stmt);
        }
    }

    //select
    @Override
    public <E> List<E> doQuery(Object parameter, BoundSql boundSql) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Statement stmt = null;
        try {
            StatementHandler handler = new SimpleStatementHandler(wrapper, parameter, boundSql);
            stmt = prepareStatement(handler, SqlType.valueOf(boundSql.getSqlType()).getParameterHandler());
            return handler.<E>query(stmt);
        } finally {
            closeStatement(stmt);
        }
    }

    private Statement prepareStatement(StatementHandler handler, ParameterHandler parameterHandler) throws SQLException {
        Statement stmt;
        Connection connection = getConnection();
        stmt = handler.prepare(connection);
        parameterHandler.parameterize(stmt, handler);
//        handler.parameterize(stmt);
        return stmt;
    }
}
