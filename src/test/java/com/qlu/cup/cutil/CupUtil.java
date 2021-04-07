package com.qlu.cup.cutil;

import com.qlu.cup.bind.Environment;
import com.qlu.cup.io.Resources;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
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
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            InputStream inputStream = Resources.getResourceAsStream("cup-conf.yml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SqlSession getSqlSession(){
        return sqlSessionFactory.getSession();
    }

    public Environment getEnvironment() {
        return sqlSessionFactory.getConfiguration().getEnvironment();
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