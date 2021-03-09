package com.qlu.cup.transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @program: cup
 * @description: 默认事务
 * @author: liuwenhao
 * @create: 2021-01-24 16:08
 **/
public class DefJdbcTransaction implements Transaction {
    protected Connection connection;
    protected DataSource dataSource;
    protected TransactionIsolationLevel level;
    protected boolean autoCommmit = true;

    public DefJdbcTransaction(DataSource ds, TransactionIsolationLevel desiredLevel) {
        this.dataSource = ds;
        this.level = desiredLevel;
    }

    public DefJdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            resetAutoCommit();
            connection.close();
        }
    }

    protected void setDesiredAutoCommit(boolean desiredAutoCommit) {
        try {
            if (connection.getAutoCommit() != desiredAutoCommit) {
                connection.setAutoCommit(desiredAutoCommit);
            }
        } catch (SQLException e) {
            throw new TransactionException("Error configuring AutoCommit.  "
                    + "Your driver may not support getAutoCommit() or setAutoCommit(). "
                    + "Requested setting: " + desiredAutoCommit + ".  Cause: " + e, e);
        }
    }

    protected void resetAutoCommit() {
        try {
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
        }
    }

    protected void openConnection() throws SQLException {
        connection = dataSource.getConnection();
        if (level != null) {
            connection.setTransactionIsolation(level.getLevel());
        }
        setDesiredAutoCommit(autoCommmit);
    }

}
