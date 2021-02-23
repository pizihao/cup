package com.qlu.cup.statement;

import com.qlu.cup.executor.Executor;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.result.ResultProcessor;
import com.qlu.cup.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


/**
 * 简单语句处理器(STATEMENT)
 * 这个类是关键，这里必须把所有的语句和参数的映射和处理完全做好
 */
public class SimpleStatementHandler extends BaseStatementHandler {

    public SimpleStatementHandler(Executor executor, Object parameter, RowBounds rowBounds, ResultProcessor resultHandler, BoundSql boundSql) {
        super(executor, parameter, rowBounds, resultHandler, boundSql);
    }

    @Override
    public int update(Statement statement) throws SQLException {
        String sql = boundSql.getSql();
        statement.execute(sql);
        return statement.getUpdateCount();
    }

    //select-->结果给ResultHandler
    @Override
    public <E> List<E> query(Statement statement, ResultProcessor resultHandler) throws SQLException {
        String sql = boundSql.getSql();
        statement.execute(sql);
        //先执行Statement.execute，然后交给ResultSetHandler.handleResultSets
        return resultSetHandler.<E>handleResultSets(statement);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        //调用Connection.createStatement
        return connection.createStatement();
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {

    }

}
