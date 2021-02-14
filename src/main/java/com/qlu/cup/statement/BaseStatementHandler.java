package com.qlu.cup.statement;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.context.ErrorContext;
import com.qlu.cup.executor.Executor;
import com.qlu.cup.executor.ExecutorException;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.mapper.ParameterHandler;
import com.qlu.cup.result.ResultProcessor;
import com.qlu.cup.result.ResultSetHandler;
import com.qlu.cup.session.RowBounds;
import com.qlu.cup.type.TypeHandlerRegistry;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 语句处理器的基类
 */
public abstract class BaseStatementHandler implements StatementHandler {

    protected final Configuration configuration;
    protected final TypeHandlerRegistry typeHandlerRegistry;
    protected final ResultSetHandler resultSetHandler;
    protected final ParameterHandler parameterHandler;

    protected final Executor executor;
    protected final RowBounds rowBounds;

    protected BoundSql boundSql;

    protected BaseStatementHandler(Executor executor, Object parameterObject, RowBounds rowBounds, ResultProcessor resultHandler, BoundSql boundSql) {
        this.configuration = Configuration.getConfiguration(boundSql.getEnvironment());
        this.executor = executor;
        this.rowBounds = rowBounds;
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.boundSql = boundSql;
        //生成parameterHandler
        this.parameterHandler = configuration.newParameterHandler(parameterObject, boundSql);
        //生成resultSetHandler
        this.resultSetHandler = configuration.newResultSetHandler(executor, rowBounds, parameterHandler, resultHandler, boundSql);
    }

    @Override
    public BoundSql getBoundSql() {
        return boundSql;
    }

    @Override
    public ParameterHandler getParameterHandler() {
        return parameterHandler;
    }

    //准备语句
    @Override
    public Statement prepare(Connection connection) throws SQLException {
        ErrorContext.instance().sql(boundSql.getSql());
        Statement statement = null;
        try {
            //实例化Statement
            statement = instantiateStatement(connection);
            return statement;
        } catch (SQLException e) {
            closeStatement(statement);
            throw e;
        } catch (Exception e) {
            closeStatement(statement);
            throw new ExecutorException("Error preparing statement.  Cause: " + e, e);
        }
    }

    //如何实例化Statement，交给子类做
    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

    //关闭语句
    protected void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            //ignore
        }
    }

}
