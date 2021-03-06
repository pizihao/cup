package com.qlu.cup.session;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.bind.Environment;
import com.qlu.cup.bind.ErrorContext;
import com.qlu.cup.executor.Executor;
import com.qlu.cup.transaction.DefJdbcTransactionFactory;
import com.qlu.cup.transaction.Transaction;
import com.qlu.cup.transaction.TransactionFactory;
import com.qlu.cup.transaction.TransactionIsolationLevel;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @program: cup
 * @description: 默认的SqlSession工厂实现
 * @author: liuwenhao
 * @create: 2021-01-24 14:53
 **/
public class DefSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefSqlSessionFactory(Configuration configuration){
        this.configuration = configuration;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
    @Override
    public SqlSession getSession() {
        return getSessionFromDataSource(null);
    }
    @Override
    public SqlSession getSession(TransactionIsolationLevel level) {
        return getSessionFromDataSource(level);
    }
    @Override
    public SqlSession getSession(Connection connection) {
        return getSessionFromConnection(connection);
    }

    private SqlSession getSessionFromDataSource(TransactionIsolationLevel level) {
        Transaction tx = null;
        try {
            final Environment environment = configuration.getEnvironment();
            final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
            tx = transactionFactory.newTransaction(environment.getDataSource(), level);
            final Executor executor = configuration.newExecutor(tx);
            return new DefSqlSession(configuration, executor);
        } catch (Exception e) {
            //如果打开事务出错，则关闭它
            closeTransaction(tx);
            throw new SqlSessionException("会话错误: " + e, e);
        } finally {
            //最后清空错误上下文
            ErrorContext.instance().reset();
        }
    }

    private SqlSession getSessionFromConnection(Connection connection) {
        try {
            boolean autoCommit;
            try {
                autoCommit = connection.getAutoCommit();
            } catch (SQLException e) {
                autoCommit = true;
            }
            final Environment environment = configuration.getEnvironment();
            final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
            final Transaction tx = transactionFactory.newTransaction(connection);
            final Executor executor = configuration.newExecutor(tx);
            return new DefSqlSession(configuration, executor, autoCommit);
        } catch (Exception e) {
            throw new SqlSessionException("会话错误: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    private TransactionFactory getTransactionFactoryFromEnvironment(Environment environment) {
        //如果没有配置事务工厂，则返回托管事务工厂
        if (environment == null || environment.getTransactionFactory() == null) {
                return new DefJdbcTransactionFactory();
        }
        return environment.getTransactionFactory();
    }

    private void closeTransaction(Transaction tx) {
        if (tx != null) {
            try {
                tx.close();
            } catch (SQLException ignore) {
            }
        }
    }
}