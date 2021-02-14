package com.qlu.cup.statement;

import com.qlu.cup.executor.Executor;
import com.qlu.cup.executor.ExecutorException;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.mapper.ParameterHandler;
import com.qlu.cup.result.ResultProcessor;
import com.qlu.cup.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 路由选择语句处理器
 */
public class RoutingStatementHandler implements StatementHandler {

    private final StatementHandler delegate;

    public RoutingStatementHandler(Executor executor, Object parameter, RowBounds rowBounds, ResultProcessor resultHandler, BoundSql boundSql) {

        //根据语句类型，委派到不同的语句处理器(STATEMENT|PREPARED|CALLABLE)
        delegate = new SimpleStatementHandler(executor, parameter, rowBounds, resultHandler, boundSql);
        throw new ExecutorException("未知的类型");
    }

    @Override
    public Statement prepare(Connection connection) throws SQLException {
        return delegate.prepare(connection);
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        delegate.parameterize(statement);
    }

    @Override
    public int update(Statement statement) throws SQLException {
        return delegate.update(statement);
    }

    @Override
    public <E> List<E> query(Statement statement, ResultProcessor resultHandler) throws SQLException {
        return delegate.<E>query(statement, resultHandler);
    }

    @Override
    public BoundSql getBoundSql() {
        return delegate.getBoundSql();
    }

    @Override
    public ParameterHandler getParameterHandler() {
        return delegate.getParameterHandler();
    }
}
