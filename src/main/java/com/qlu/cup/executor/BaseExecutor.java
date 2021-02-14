package com.qlu.cup.executor;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.context.Environment;
import com.qlu.cup.context.ErrorContext;
import com.qlu.cup.logging.Log;
import com.qlu.cup.logging.LogFactory;
import com.qlu.cup.logging.jdbc.ConnectionLogger;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.result.ResultProcessor;
import com.qlu.cup.session.RowBounds;
import com.qlu.cup.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 执行器基类
 */
public abstract class BaseExecutor implements Executor {

    private static final Log log = LogFactory.getLog(BaseExecutor.class);

    protected Transaction transaction;
    protected Executor wrapper;

    protected Environment environment;

    //查询堆栈
    protected int queryStack = 0;
    private boolean closed;

    protected BaseExecutor(Environment environment, Transaction transaction) {
        this.transaction = transaction;
        this.closed = false;
        this.environment = environment;
        this.wrapper = this;
    }

    @Override
    public Transaction getTransaction() {
        if (closed) {
            throw new ExecutorException("Executor was closed.");
        }
        return transaction;
    }

    @Override
    public void close(boolean forceRollback) {
        try {
            try {
                rollback(forceRollback);
            } finally {
                if (transaction != null) {
                    transaction.close();
                }
            }
        } catch (SQLException e) {
            // Ignore.  There's nothing that can be done at this point.
            log.warn("Unexpected exception on closing transaction.  Cause: " + e);
        } finally {
            transaction = null;
            closed = true;
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    //SqlSession.update/insert/delete会调用此方法
    @Override
    public int update(BoundSql boundSql, Object parameter) throws SQLException {
        ErrorContext.instance().resource(boundSql.getHandle()).activity("executing an update").object(boundSql.getID());
        if (closed) {
            throw new ExecutorException("Executor was closed.");
        }
        //先清局部缓存，再更新，如何更新交由子类，模板方法模式
        return doUpdate(boundSql, parameter);
    }

    //SqlSession.selectList会调用此方法
    @Override
    public <E> List<E> query(BoundSql boundSql, Object parameter, RowBounds rowBounds, ResultProcessor resultHandler) throws SQLException {
        //查询
        return query(parameter, rowBounds, resultHandler, boundSql);
    }

    public <E> List<E> query(Object parameter, RowBounds rowBounds, ResultProcessor resultHandler, BoundSql boundSql) throws SQLException {
        ErrorContext.instance().resource(boundSql.getHandle()).activity("executing a query").object(boundSql.getID());
        //如果已经关闭，报错
        if (closed) {
            throw new ExecutorException("Executor was closed.");
        }
        List<E> list;
        try {
            //加一,这样递归调用到上面的时候就不会再清局部缓存了
            queryStack++;
            list = queryFromDatabase(parameter, rowBounds, resultHandler, boundSql);
        } finally {
            //清空堆栈
            queryStack--;
        }
        return list;
    }

    @Override
    public void commit(boolean required) throws SQLException {
        if (closed) {
            throw new ExecutorException("Cannot commit, transaction is already closed");
        }
        if (required) {
            transaction.commit();
        }
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        if (!closed) {
            if (required) {
                transaction.rollback();
            }
        }
    }

    protected abstract int doUpdate(BoundSql ms, Object parameter)
            throws SQLException;

    //query-->queryFromDatabase-->doQuery
    protected abstract <E> List<E> doQuery(Object parameter, RowBounds rowBounds, ResultProcessor resultHandler, BoundSql boundSql)
            throws SQLException;

    protected void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }


    //从数据库查
    private <E> List<E> queryFromDatabase(Object parameter, RowBounds rowBounds, ResultProcessor processor, BoundSql boundSql) throws SQLException {
        return doQuery(parameter, rowBounds, processor, boundSql);
    }

    protected Connection getConnection(Log statementLog) throws SQLException {
        Connection connection = transaction.getConnection();
        if (statementLog.isDebugEnabled()) {
            return ConnectionLogger.newInstance(connection, statementLog, queryStack);
        } else {
            return connection;
        }
    }

    @Override
    public void setExecutorWrapper(Executor wrapper) {
        this.wrapper = wrapper;
    }

}
