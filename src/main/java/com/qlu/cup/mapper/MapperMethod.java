package com.qlu.cup.mapper;

import com.qlu.cup.bind.BindException;
import com.qlu.cup.bind.Configuration;
import com.qlu.cup.result.ResultProcessor;
import com.qlu.cup.session.RowBounds;
import com.qlu.cup.session.SqlSession;
import com.qlu.cup.util.PartsUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 * @author Lasse Voss
 */

/**
 * 映射器方法
 *
 */
public class MapperMethod {

  private final SqlCommand command;
  private final MethodSignature method;

  public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
    this.command = new SqlCommand(config, mapperInterface, method);
    this.method = new MethodSignature(config, method);
  }

  //执行
  public Object execute(SqlSession sqlSession, Object[] args) {
    Object result;
    //可以看到执行时就是4种情况，insert|update|delete|select，分别调用SqlSession的4大类方法
    if (PartsUtil.INSERT.equals(command.getType())) {
      Object param = method.convertArgsToSqlCommandParam(args);
      result = rowCountResult(sqlSession.insert(command.getName(), param));
    } else if (PartsUtil.UPDATE.equals(command.getType())) {
      Object param = method.convertArgsToSqlCommandParam(args);
      result = rowCountResult(sqlSession.update(command.getName(), param));
    } else if (PartsUtil.DELETE.equals(command.getType())) {
      Object param = method.convertArgsToSqlCommandParam(args);
      result = rowCountResult(sqlSession.delete(command.getName(), param));
    } else if (PartsUtil.SELECT.equals(command.getType())) {
      if (method.returnsVoid() && method.hasResultHandler()) {
        //如果有结果处理器
        executeWithResultHandler(sqlSession, args);
        result = null;
      } else if (method.returnsMany()) {
        //如果结果有多条记录
        result = executeForMany(sqlSession, args);
      } else if (method.returnsMap()) {
        //如果结果是map
        throw new BindException("不支持的返回类型");
      } else {
        //否则就是一条记录
        Object param = method.convertArgsToSqlCommandParam(args);
        result = sqlSession.selectOne(command.getName(), param);
      }
    } else {
      throw new BindException("Unknown execution method for: " + command.getName());
    }
    if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
      throw new BindException("Mapper method '" + command.getName()
          + " attempted to return null from a method with a primitive return type (" + method.getReturnType() + ").");
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
      result = Integer.valueOf(rowCount);
    } else if (Long.class.equals(method.getReturnType()) || Long.TYPE.equals(method.getReturnType())) {
      //如果返回值是大long或小long
      result = Long.valueOf(rowCount);
    } else if (Boolean.class.equals(method.getReturnType()) || Boolean.TYPE.equals(method.getReturnType())) {
      //如果返回值是大boolean或小boolean
      result = Boolean.valueOf(rowCount > 0);
    } else {
      throw new BindException("Mapper method '" + command.getName() + "' has an unsupported return type: " + method.getReturnType());
    }
    return result;
  }

  //结果处理器
  private void executeWithResultHandler(SqlSession sqlSession, Object[] args) {
    BoundSql ms = sqlSession.getConfiguration().getSqlMap().get(command.getName());
    if (void.class.equals(ms.getResultType())) {
      throw new BindException("method " + command.getName()
          + " needs resultType attribute in YML so a ResultProcessor can be used as a parameter.");
    }
    Object param = method.convertArgsToSqlCommandParam(args);
    if (method.hasRowBounds()) {
      RowBounds rowBounds = method.extractRowBounds(args);
      sqlSession.select(command.getName(), param, rowBounds, method.extractResultHandler(args));
    } else {
      sqlSession.select(command.getName(), param, method.extractResultHandler(args));
    }
  }

  //多条记录
  private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
    List<E> result;
    Object param = method.convertArgsToSqlCommandParam(args);
    //代入RowBounds
    if (method.hasRowBounds()) {
      RowBounds rowBounds = method.extractRowBounds(args);
      result = sqlSession.<E>selectList(command.getName(), param, rowBounds);
    } else {
      result = sqlSession.<E>selectList(command.getName(), param);
    }
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
    return BoundSql.getClazz(method.getReturnType().toString());
  }

  @SuppressWarnings("unchecked")
  private <E> E[] convertToArray(List<E> list) {
    E[] array = (E[]) Array.newInstance(method.getReturnType().getComponentType(), list.size());
    array = list.toArray(array);
    return array;
  }
}
