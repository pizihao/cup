package com.qlu.cup.transaction;


import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * @program: cup
 * @description: 事务工厂
 * @author: liuwenhao
 * @create: 2021-01-24 16:07
 **/
public interface TransactionFactory {

    //设置属性
    void setProperties(Properties props);

    //根据Connection创建Transaction
    Transaction newTransaction(Connection conn);

    //根据数据源和事务隔离级别创建Transaction
    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);

}