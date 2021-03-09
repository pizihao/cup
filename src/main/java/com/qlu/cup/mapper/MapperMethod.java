package com.qlu.cup.mapper;

import com.qlu.cup.bind.BindException;
import com.qlu.cup.bind.Configuration;
import com.qlu.cup.session.SqlSession;
import com.qlu.cup.util.PartsUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 映射器方法
 */
public class MapperMethod {

    private final SqlCommand command;
    private final MethodSignature method;
    private final Configuration configuration;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
        this.command = new SqlCommand(config, mapperInterface, method);
        this.method = new MethodSignature(method);
        this.configuration = config;
    }

    //执行
    public Object execute(SqlSession sqlSession, Object[] args) throws IllegalAccessException {
        Object result = null;
        //如果有参数的话处理参数
        if (PartsUtil.INSERT.equals(command.getType())) {
            Object param = method.convertArgsToSqlCommandParam(configuration, command.getName(), args);
            result = rowCountResult(sqlSession.insert(command.getName(), param));
        } else if (PartsUtil.UPDATE.equals(command.getType())) {
            Object param = method.convertArgsToSqlCommandParam(configuration, command.getName(), args);
            result = rowCountResult(sqlSession.update(command.getName(), param));
        } else if (PartsUtil.DELETE.equals(command.getType())) {
            Object param = method.convertArgsToSqlCommandParam(configuration, command.getName(), args);
            result = rowCountResult(sqlSession.delete(command.getName(), param));
        } else if (PartsUtil.SELECT.equals(command.getType())) {
            if (method.returnsMany()) {
                //如果结果有多条记录
                result = executeForMany(sqlSession, args);
            } else {
                //否则就是一条记录
                Object param = method.convertArgsToSqlCommandParam(configuration, command.getName(), args);
                result = sqlSession.select(command.getName(), param);
            }
        } else {
            throw new BindException("未知的执行方法: " + command.getName());
        }
        if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
            throw new BindException("映射方法 '" + command.getName()
                    + " 试图从具有原始返回类型的方法返回null (" + method.getReturnType() + ").");
        }

        return result;
    }

    //这个方法对返回值的类型进行了一些检查，使得更安全
    private Object rowCountResult(int rowCount) {
        final Object result;
        if (method.returnsVoid()) {
            result = null;
        } else if (Integer.class.equals(method.getReturnType()) || Integer.TYPE.equals(method.getReturnType())) {
            //如果返回值是大int或小int
            result = rowCount;
        } else if (Long.class.equals(method.getReturnType()) || Long.TYPE.equals(method.getReturnType())) {
            //如果返回值是大long或小long
            result = (long) rowCount;
        } else if (Boolean.class.equals(method.getReturnType()) || Boolean.TYPE.equals(method.getReturnType())) {
            //如果返回值是大boolean或小boolean
            result = rowCount > 0;
        } else {
            throw new BindException("Mapper method '" + command.getName() + "' has an unsupported return type: " + method.getReturnType());
        }
        return result;
    }

    //结果处理器
    private void executeWithResultHandler(SqlSession sqlSession, Object[] args) throws IllegalAccessException {
        BoundSql ms = sqlSession.getConfiguration().getSqlMap().get(command.getName());
        if (void.class.equals(ms.getResultType())) {
            throw new BindException("method " + command.getName()
                    + " needs resultType attribute in YML so a ResultProcessor can be used as a parameter.");
        }
        Object param = method.convertArgsToSqlCommandParam(configuration, command.getName(), args);
        sqlSession.select(command.getName(), param);
    }

    //多条记录
    private <E> Object executeForMany(SqlSession sqlSession, Object[] args) throws IllegalAccessException {
        List<E> result = null;
        Object param = method.convertArgsToSqlCommandParam(configuration, command.getName(), args);
        result = sqlSession.<E>selectList(command.getName(), param);
        if (!method.getReturnType().isAssignableFrom(result.getClass())) {
            if (method.getReturnType().isArray()) {
                return convertToArray(result);
            } else {
                return convertToDeclaredCollection(sqlSession.getConfiguration(), result);
            }
        }
        return result;
    }

    private <E> Object convertToDeclaredCollection(Configuration config, List<E> list) {
        return ObjectInterface.resolveInterface(method.getReturnType());
    }

    @SuppressWarnings("unchecked")
    private <E> E[] convertToArray(List<E> list) {
        E[] array = (E[]) Array.newInstance(method.getReturnType().getComponentType(), list.size());
        array = list.toArray(array);
        return array;
    }
}
