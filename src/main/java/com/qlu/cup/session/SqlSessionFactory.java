package com.qlu.cup.session;

import com.qlu.cup.transaction.TransactionIsolationLevel;

import java.sql.Connection;

/**
 * @program: cup
 * @description: 创建一个sql会话的工厂
 * @author: liuwenhao
 * @create: 2021-01-23 17:48
 **/
public interface SqlSessionFactory {
    SqlSession getSession();

    SqlSession getSession(boolean autoCommit);

    SqlSession getSession(Connection connection);

    SqlSession getSession(TransactionIsolationLevel level);

}