package com.qlu.cup.statement;

import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.mapper.ParameterHandler;
import com.qlu.cup.result.ResultProcessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author Clinton Begin
 */

/**
 * 语句处理器
 */
public interface StatementHandler {

    //准备语句
    Statement prepare(Connection connection)
            throws SQLException;

    //参数化
    void parameterize(Statement statement)
            throws SQLException;

    //update
    int update(Statement statement)
            throws SQLException;

    <E> List<E> query(Statement statement, ResultProcessor resultHandler)
            throws SQLException;

    //得到绑定sql
    BoundSql getBoundSql();

    //得到参数处理器
    ParameterHandler getParameterHandler();

}
