package com.qlu.cup.statement;

import com.qlu.cup.mapper.BoundSql;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 语句处理器
 */
public interface StatementHandler {

  /**
   * 准备语句
   **/
  Statement prepare(Connection connection)
      throws SQLException;
  /**
   * 参数化
   **/
  void parameterize(Statement statement)
      throws SQLException;
  /**
   * 批处理
   **/
  void batch(Statement statement)
      throws SQLException;
  /**
   * update
   **/
  int update(Statement statement)
      throws SQLException;
  /**
   * select-->结果给ResultHandler
   **/
  <E> List<E> query(Statement statement)
          throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException;

  /**
   * 得到绑定sql
   **/
  BoundSql getBoundSql();

}
