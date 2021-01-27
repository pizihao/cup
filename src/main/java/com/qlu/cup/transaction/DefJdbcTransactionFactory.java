package com.qlu.cup.transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * @program: cup
 * @description: 默认事务工厂
 * @author: liuwenhao
 * @create: 2021-01-24 16:08
 **/
public class DefJdbcTransactionFactory implements TransactionFactory {

    @Override
    public void setProperties(Properties props) {
    }

    @Override
    public Transaction newTransaction(Connection conn) {
        return new DefJdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new DefJdbcTransaction(dataSource, level, autoCommit);
    }
}