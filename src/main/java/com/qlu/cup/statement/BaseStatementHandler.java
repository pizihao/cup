package com.qlu.cup.statement;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.bind.ErrorContext;
import com.qlu.cup.executor.Executor;
import com.qlu.cup.executor.ExecutorException;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.util.PartsUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 语句处理器的基类
 */
public abstract class BaseStatementHandler implements StatementHandler {

    protected final Configuration configuration;

    protected final Executor executor;

    protected BoundSql boundSql;

    protected Object parameterObject;

    protected BaseStatementHandler(Executor executor, Object parameterObject, BoundSql boundSql) {
        this.configuration = boundSql.getConfiguration();
        this.executor = executor;
        this.boundSql = boundSql;
        this.parameterObject = parameterObject;
    }

    @Override
    public BoundSql getBoundSql() {
        return boundSql;
    }

    @Override
    public Statement prepare(Connection connection) throws SQLException {
        ErrorContext.instance().sql(boundSql.getSql());
        Statement statement = null;
        try {
            //实例化Statement
            statement = instantiateStatement(connection);
            //设置超时
            setStatementTimeout(statement);
            //设置读取条数
            setFetchSize(statement);
            return statement;
        } catch (SQLException e) {
            closeStatement(statement);
            throw e;
        } catch (Exception e) {
            closeStatement(statement);
            throw new ExecutorException("Error preparing statement.  Cause: " + e, e);
        }
    }

    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

    protected void setStatementTimeout(Statement stmt) throws SQLException {
        stmt.setQueryTimeout(PartsUtil.timeout);
    }

    protected void setFetchSize(Statement stmt) throws SQLException {
        stmt.setFetchSize(PartsUtil.fetchSize);
    }

    //关闭语句
    protected void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException ignored) {
        }
    }
}
