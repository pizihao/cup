package com.qlu.cup.cutil;

import com.qlu.cup.builder.yml.YNode;
import com.qlu.cup.builder.yml.YmlMapperBuilder;
import com.qlu.cup.builder.yml.YmlMapperRead;
import com.qlu.cup.conf.InputConf;
import com.qlu.cup.context.Environment;
import com.qlu.cup.io.Resources;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.qlu.cup.session.DefSqlSessionFactory;
import com.qlu.cup.session.SqlSession;
import com.qlu.cup.session.SqlSessionFactory;
import com.qlu.cup.session.SqlSessionFactoryBuilder;

/**
 * @program: cup
 * @description: 测试数据读取
 * @author: liuwenhao
 * @create: 2021-01-24 12:59
 **/
public class CupUtil {
    private static DataSource datasource;
    private static Environment environment;
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            InputStream inputStream = Resources.getResourceAsStream("cup-conf.yml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//            InputStream inputStream = Resources.getResourceAsStream("mapper/UserMapper.yml");
//            Map<Class<?>, YNode> builder = new YmlMapperBuilder().builder(inputStream);
//            builder.forEach((aClass, yNode) -> {
//                System.out.println(yNode.getId());
//                System.out.println(yNode.getName());
//                System.out.println(yNode.getNamespace());
//                System.out.println(yNode.getParameterType());
//                System.out.println(yNode.getResultType());
//                System.out.println(yNode.getRootName());
//                System.out.println(yNode.getSql());
//                System.out.println(yNode.getValue());
//                System.out.println("+++++++++++++++");
//                yNode.getNode().forEach((s, yNode1) -> {
//                    System.out.println("=====================");
//                    System.out.println(s);
//                    System.out.println(yNode1.getId());
//                    System.out.println(yNode1.getName());
//                    System.out.println(yNode1.getNamespace());
//                    System.out.println(yNode1.getParameterType());
//                    System.out.println(yNode1.getResultType());
//                    System.out.println(yNode1.getRootName());
//                    System.out.println(yNode1.getSql());
//                    System.out.println(yNode1.getValue());
//                    System.out.println("=====================");
//                });
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SqlSession getSqlSession(){
        return sqlSessionFactory.getSession();
    }

    public Environment getEnvironment() {
        return environment;
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