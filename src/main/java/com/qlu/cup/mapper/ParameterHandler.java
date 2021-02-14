package com.qlu.cup.mapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 参数处理器
 */
public interface ParameterHandler {

  //得到参数
  Object getParameterObject();

  //设置参数
  void setParameters(PreparedStatement ps)
      throws SQLException;

}
