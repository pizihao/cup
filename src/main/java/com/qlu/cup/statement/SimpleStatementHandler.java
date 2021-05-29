package com.qlu.cup.statement;

import com.qlu.cup.builder.yml.MapperException;
import com.qlu.cup.executor.Executor;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.parameter.ParameterMapping;
import com.qlu.cup.result.ResultSetHandler;
import com.qlu.cup.util.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class SimpleStatementHandler extends BaseStatementHandler {

    public SimpleStatementHandler(Executor executor, Object parameter, BoundSql boundSql) {
        super(executor, parameter, boundSql);
    }

    @Override
    public int update(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        int result = ps.getUpdateCount();
        //添加自增id
        if (boundSql.getGeneratedKey()){
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                result = generatedKeys.getInt(1);
            }
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
    }
}
