package com.qlu.cup.session;

import com.qlu.cup.result.ResultProcessor;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * @program: cup
 * @description: 会话
 * @author: liuwenhao
 * @create: 2021-01-23 17:52
 **/
public interface SqlSession extends Closeable {

    /**
     * 根据指定的SqlID获取一条记录的封装对象
     * @param <T>  封装之后的对象类型
     * @param statement sqlID
     * @return Mapped object 封装之后的对象
     */
    <T> T selectOne(String statement);

    /**
     * 根据指定的SqlID获取一条记录的封装对象，只不过这个方法容许我们可以给sql传递一些参数
     * 一般在实际使用中，这个参数传递的是pojo，或者Map或者ImmutableMap
     * @param <T> the returned object type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @return Mapped object
     */
    <T> T selectOne(String statement, Object parameter);

    /**
     * 根据指定的sqlId获取多条记录
     * @param <E> the returned list element type
     * @param statement Unique identifier matching the statement to use.
     * @return List of mapped object
     */
    <E> List<E> selectList(String statement);

    /**
     * 获取多条记录，这个方法容许我们可以传递一些参数
     * @param <E> the returned list element type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @return List of mapped object
     */
    <E> List<E> selectList(String statement, Object parameter);

    <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);
    /**
     * 将查询到的结果列表转换为Map类型。
     * @param <K> the returned Map keys type
     * @param <V> the returned Map values type
     * @param statement Unique identifier matching the statement to use.
     * @param mapKey The property to use as key for each value in the list. 这个参数会作为结果map的key
     * @return Map containing key pair data.
     */
    <K, V> Map<K, V> selectMap(String statement, String mapKey);

    /**
     * 将查询到的结果列表转换为Map类型。这个方法容许我们传入需要的参数
     * @param <K> the returned Map keys type
     * @param <V> the returned Map values type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @param mapKey The property to use as key for each value in the list.
     * @return Map containing key pair data.
     */
    <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey);


    <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds);
    /**
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @param processor ResultProcessor that will handle each retrieved row
     * @return Mapped object
     */
    void select(String statement, Object parameter, ResultProcessor processor);

    /**
     * 获取一条记录,并转交给ResultProcessor处理。这个方法容许我们自己定义对
     * 查询到的行的处理方式。不过一般用的并不是很多
     * @param statement Unique identifier matching the statement to use.
     * @param processor ResultProcessor that will handle each retrieved row
     * @return Mapped object
     */
    void select(String statement, ResultProcessor processor);


    void select(String statement, Object parameter, RowBounds rowBounds, ResultProcessor processor);

    /**
     * 插入记录，容许传入参数。
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return int The number of rows affected by the insert. 注意返回的是受影响的行数
     */
    int insert(String statement, Object parameter);

    /**
     * 更新记录。返回的是受影响的行数
     * @param statement Unique identifier matching the statement to execute.
     * @return int The number of rows affected by the update.
     */
    int update(String statement);

    /**
     * 更新记录
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return 返回的是受影响的行数
     */
    int update(String statement, Object parameter);

    /**
     * 删除记录
     * @param 执行的语句
     * @return 返回的是受影响的行数
     */
    int delete(String statement);

    /**
     * 删除记录
     * @param statement 执行的语句
     * @param parameter 参数对象
     * @return 返回的是受影响的行数
     */
    int delete(String statement, Object parameter);

    void commit();

    void commit(boolean force);

    void rollback();

    void rollback(boolean force);

    /**
     * 关闭Session
     */
    @Override
    void close();

    /**
     * 清理Session缓存
     */
    void clearCache();
}