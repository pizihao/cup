package com.qlu.cup.statement;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * 定义加入参数的方法
 *
 * @program: cup
 * @author: liuwenhao
 * @create: 2021-05-29 13:24
 **/
@FunctionalInterface
public interface ParameterHandler {

    /**
     * 语句处理
     *
     * @param statement statement
     * @param handler handler
     * @throws SQLException SQL异常
     * @author liuwenhao
     * @date 2021/5/29 13:26
     */
    void parameterize(Statement statement, StatementHandler handler) throws SQLException;
}
