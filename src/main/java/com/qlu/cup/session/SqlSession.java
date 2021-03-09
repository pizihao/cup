package com.qlu.cup.session;

import com.qlu.cup.bind.Configuration;

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;

/**
 * @program: cup
 * @description: SqlSession
 * @author: liuwenhao
 * @create: 2021-03-03 15:09
 **/
public interface SqlSession extends Closeable {

    /**
     * 根据指定的SqlID获取一条记录的封装对象，只不过这个方法容许我们可以给sql传递一些参数
     */
    <T> T select(String statement, Object parameter);

    /**
     * 获取多条记录，这个方法容许我们可以传递一些参数
     */
    <E> List<E> selectList(String statement, Object parameter);


    /**
     * 插入记录，容许传入参数。
     */
    int insert(String statement, Object parameter);

    /**
     * 更新记录 返回的是受影响的行数
     */
    int update(String statement, Object parameter);


    /**
     * 删除记录 返回的是受影响的行数
     */
    int delete(String statement, Object parameter);

    void commit();

    void rollback();

    public Connection getConnection();

    Configuration getConfiguration();
    /**
     * 关闭Session
     */
    @Override
    void close();

    <T> T getMapper(Class<T> type);
}