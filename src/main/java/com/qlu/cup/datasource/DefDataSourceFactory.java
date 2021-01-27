package com.qlu.cup.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @program: cup
 * @description: 数据源工厂
 * @author: liuwenhao
 * @create: 2021-01-25 21:53
 **/
public class DefDataSourceFactory implements DataSourceFactory{
    private static DataSource dataSource;

    @Override
    public void setProperties(Properties props) {
        if (props != null){
            try {
                dataSource = DruidDataSourceFactory.createDataSource(props);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}