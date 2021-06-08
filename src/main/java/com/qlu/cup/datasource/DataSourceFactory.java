package com.qlu.cup.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @program: cup
 * @description: 数据源工厂
 * @author: liuwenhao
 * @create: 2021-01-25 21:53
 **/
public interface DataSourceFactory {

  void setProperties(Properties props) throws Exception;

  DataSource getDataSource();

}
