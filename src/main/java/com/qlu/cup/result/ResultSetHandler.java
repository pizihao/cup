package com.qlu.cup.result;

import com.qlu.cup.builder.yml.YmlMapperBuilder;
import com.qlu.cup.exception.CupException;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.util.ReflectUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: cup
 * @description: 数据库返回结果处理
 * @author: liuwenhao
 * @create: 2021-03-05 21:03
 **/
public class ResultSetHandler {
    /**
     * @param statement statement对象，必须已经执行过SQL，可以从这里面拿到数据
     * @return java.util.List<E>
     * @description: TODO
     * @author liuwenaho
     * @date 2021/3/5 21:04
     */
    public static <E> List<E> handlerResultSelect(Statement statement, BoundSql boundSql) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        ResultSet resultSet = statement.getResultSet();
        ResultType resultType = boundSql.getResultType();

        String ymlResult = resultType.getResultType();
        String methodResult = ReflectUtil.genericReturnType(boundSql.getResult());
        String instanceResultType = ReflectUtil.returnStringType(ymlResult, methodResult);
        List<Object> eList = new ArrayList<>();
        //挨个查看返回结果
        Class<?> aClass = Class.forName(instanceResultType);
        //拿到属性
        //每次通过反射生成一个实体类对象
        Field[] allFields = YmlMapperBuilder.getAllFields(aClass);
        while (resultSet.next()) {
            //生成一个对象
            Object instance = aClass.newInstance();
            //获取类加载器
            if (aClass.getClassLoader() == null) {
                //java提供的类的情况
                instance = resultSet.getObject(1);
            } else {
                for (Map.Entry<String, ResultMapping> entry : resultType.getResultMap().entrySet()) {
                    try {
                        //拿到结果的位置
                        for (Field allField : allFields) {
                            if (allField.getName().equals(entry.getKey())) {
                                int column = resultSet.findColumn(entry.getKey());
                                //这个位置真正的值
                                Object object = getObject(column, allField.getType(), resultSet);
                                //通过setter把后去的值放进去
                                String methodName = "set" + allField.getName().substring(0, 1).toUpperCase() + allField.getName().substring(1);
                                try {
                                    Method method = instance.getClass().getMethod(methodName, allField.getType());
                                    method.invoke(instance, object);
                                } catch (NoSuchMethodException | IllegalAccessException e) {
                                    try {
                                        //在设置属性值的时候cup不允许出现找不到方法这个异常，cup需要在在这个时候尝试属性直接赋值
                                        //判断属性是否是private
                                        allField.set(instance, object);
                                    } catch (IllegalAccessException ignored) {
                                        //这样还不成功就是null吧
                                    }
                                }
                            }
                        }
                    } catch (SQLException ignored) {
                        //属性和数据库字段不匹配的情况的情况,这里不能归类为一个异常
                    }
                }
            }
            eList.add(instance);
        }

        boundSql.getConfiguration().getCupCache().putCache(boundSql.getNamespace(), boundSql.getName(), eList);
        return (List<E>) eList;
    }

    public static <T> T getObject(int columnIndex, Class<T> type, ResultSet resultSet) throws SQLException {
        if (type == null) {
            throw new CupException("类型不能为null");
        }

        if (type.equals(Struct.class)) {
            throw new SQLFeatureNotSupportedException();
        } else if (type.equals(RowId.class)) {
            return (T) resultSet.getRowId(columnIndex);
        } else if (type.equals(NClob.class)) {
            return (T) resultSet.getNClob(columnIndex);
        } else if (type.equals(SQLXML.class)) {
            return (T) resultSet.getSQLXML(columnIndex);
        } else if (type.equals(String.class)) {
            return (T) resultSet.getString(columnIndex);
        } else if (type.equals(BigDecimal.class)) {
            return (T) resultSet.getBigDecimal(columnIndex);
        } else if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
            return (T) Boolean.valueOf(resultSet.getBoolean(columnIndex));
        } else if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return (T) Integer.valueOf(resultSet.getInt(columnIndex));
        } else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return (T) Long.valueOf(resultSet.getLong(columnIndex));
        } else if (type.equals(Float.class) || type.equals(Float.TYPE)) {
            return (T) Float.valueOf(resultSet.getFloat(columnIndex));
        } else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
            return (T) Double.valueOf(resultSet.getDouble(columnIndex));
        } else if (type.equals(byte[].class)) {
            return (T) resultSet.getBytes(columnIndex);
        } else if (type.equals(Date.class)) {
            return (T) resultSet.getDate(columnIndex);
        } else if (type.equals(Time.class)) {
            return (T) resultSet.getTime(columnIndex);
        } else if (type.equals(Timestamp.class)) {
            return (T) resultSet.getTimestamp(columnIndex);
        } else if (type.equals(Clob.class)) {
            return (T) resultSet.getClob(columnIndex);
        } else if (type.equals(Blob.class)) {
            return (T) resultSet.getBlob(columnIndex);
        } else if (type.equals(Array.class)) {
            return (T) resultSet.getArray(columnIndex);
        } else if (type.equals(Ref.class)) {
            return (T) resultSet.getRef(columnIndex);
        } else if (type.equals(URL.class)) {
            return (T) resultSet.getURL(columnIndex);
        } else {
            throw new CupException("不支持的类型" + type.getName());
        }
    }
}