package com.qlu.cup.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据源工厂
 */
public interface DataSourceFactory {

  //设置属性,被XMLConfigBuilder所调用
  void setProperties(Properties props) throws Exception;

  DataSource getDataSource();

}
