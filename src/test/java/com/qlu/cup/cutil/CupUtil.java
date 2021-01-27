package com.qlu.cup.cutil;

import com.qlu.cup.conf.InputConf;
import com.qlu.cup.io.Resources;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSourceFactory;

/**
 * @program: cup
 * @description: 测试数据读取
 * @author: liuwenhao
 * @create: 2021-01-24 12:59
 **/
public class CupUtil {
    private static DataSource datasource;

    static {
        try {
//            Properties prop = new Properties();
            InputStream inputStream = Resources.getResourceAsStream("cup-conf.yml");
            InputConf inputConf = new InputConf();
            inputConf.setInputStream(inputStream);
            //读取配置文件
//            prop.load(inputStream);
//            System.out.println(prop);
//            datasource = DruidDataSourceFactory.createDataSource(prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接对象（从数据库连接池中获取）
     *
     * @return 连接对象
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = datasource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 关闭jdbc资源对象
     *
     * @param connection 连接对象
     */
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}