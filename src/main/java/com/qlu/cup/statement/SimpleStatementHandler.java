package com.qlu.cup.statement;

import com.qlu.cup.builder.yml.MapperException;
import com.qlu.cup.executor.Executor;
import com.qlu.cup.logging.Log;
import com.qlu.cup.logging.log4j.Log4jImpl;
import com.qlu.cup.logging.nologging.NoLoggingImpl;
import com.qlu.cup.executor.ExecutorException;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.mapper.SqlType;
import com.qlu.cup.parameter.ParameterMapping;
import com.qlu.cup.result.ResultSetHandler;
import com.qlu.cup.util.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class SimpleStatementHandler extends BaseStatementHandler {

    private static Log log;

    public SimpleStatementHandler(Executor executor, Object parameter, BoundSql boundSql) {
        super(executor, parameter, boundSql);
        if (boundSql.getConfiguration().isLog()) {
            log = new Log4jImpl(SimpleStatementHandler.class);
        } else {
            log = new NoLoggingImpl(SimpleStatementHandler.class);
        }
    }

    @Override
    public int update(Statement statement) throws SQLException {
        int result = 0;
        if (boundSql.getSqlType().equals(SqlType.DML.getName())) {
            PreparedStatement ps = (PreparedStatement) statement;
            ps.execute();
            result = ps.getUpdateCount();
            //添加自增id
            if (Boolean.TRUE.equals(boundSql.getGeneratedKey())) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    result = generatedKeys.getInt(1);
                }
            }
            return result;
        }
        if (boundSql.getSqlType().equals(SqlType.DDL.getName())) {
            statement.execute(boundSql.getSql());
            result = statement.getUpdateCount();
            return result;
        }
        if (boundSql.getSqlType().equals(SqlType.DCL.getName())) {
            throw new ExecutorException("不支持的sql类型:DCL");
        }
        return result;
    }

    @Override
    public void batch(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.addBatch();
    }

    @Override
    public <E> List<E> query(Statement statement) throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return ResultSetHandler.<E>handlerResultSelect(ps, boundSql);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        if (boundSql.getResultType() != null) {
            return connection.prepareStatement(boundSql.getSql(), PreparedStatement.RETURN_GENERATED_KEYS);
        } else {
            return connection.prepareStatement(boundSql.getSql());
        }
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        //判断是否存在参数
        if (parameterObject == null) {
            return;
        }
        PreparedStatement ps = (PreparedStatement) statement;
        //拿到传入的参数
        Map<String, String> parameterMap = ReflectUtil.mapStringToMap(parameterObject.toString());
        //拿到参数对应的类型和名字
        Map<String, ParameterMapping> mappingMap = boundSql.getParameterMap();
        //获取参数对应的位置
        Map<String, Integer> parameterIndex = boundSql.getParameterIndex();
        //cup需要根据位置设置对应的参数
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            //找到对应key在parameterIndex中的位置
            String name = entry.getKey();
            //判断是否存在这个参数的映射
            if (mappingMap.get(name) == null || parameterIndex.get(name) == null) {
                throw new MapperException("无法映射的参数:" + name);
            }
            //判断类加载器
            if (mappingMap.get(name).getJavaType().getClassLoader() == null) {
                //根据parameterMap和parameterIndex直接注入参数
                ps.setObject(parameterIndex.get(name), parameterMap.get(name));
            }
        }
        log.info("sql:" + boundSql.getSql());
        log.info("参数:" + parameterObject.toString());
    }

    @Override
    public void parameterizeToDML(Statement statement) {
        //判断参数
        if (parameterObject == null) {
            return;
        }
        //获取现在的sql语句
        String sql = boundSql.getSql();
        StringBuilder builder = new StringBuilder(sql);
        //获取参数
        Map<String, String> parameterMap = ReflectUtil.mapStringToMap(parameterObject.toString());
        //参数的位置
        Map<String, Integer> parameterIndex = boundSql.getParameterIndex();
        //参数对应的类型和名字
        Map<String, ParameterMapping> mappingMap = boundSql.getParameterMap();
        //进行字符串的替换
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            //找到对应key在parameterIndex中的位置
            String name = entry.getKey();
            //判断是否存在这个参数的映射
            if (mappingMap.get(name) == null || parameterIndex.get(name) == null) {
                throw new MapperException("无法映射的参数:" + name);
            }
            //找到占位符对应的位置
            int index = ReflectUtil.getIndex(sql, parameterIndex.get(name));
            //把这个位置的占位替换为参数
            builder.replace(index, index + 1, parameterMap.get(name));
        }
        boundSql.setSql(builder.toString());
    }
}
