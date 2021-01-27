package com.qlu.cup.cutil;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @program: cup
 * @description: 操作数据库工具类
 * @author: liuwenhao
 * @create: 2021-01-25 18:17
 **/
public class DbUtil {
    /**
     * 查询一条记录
     */
    public static void selectOne() {
        //创建QueryRunner 对象
        QueryRunner queryRunner = new QueryRunner();
        //获取Connection连接
        Connection connection = CupUtil.getConnection();
        //定义sql语句
        String sql = "select * from person where id=?";
        try {
            assert connection != null;
            //这条记录返回Map集合
            Map<String, Object> objectMap = queryRunner.query(connection, sql, new MapHandler(), 2);
            System.out.println(objectMap);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            CupUtil.close(connection);
        }
    }

    /**
     * 查询多条记录
     */
    public static void selectList() {
        //创建QueryRunner 对象
        QueryRunner queryRunner = new QueryRunner();
        //获取Connection连接
        Connection connection = CupUtil.getConnection();
        //定义sql语句
        String sql = "select * from person";
        try {
            //这条记录返回Map集合
            List<Map<String, Object>> mapList = queryRunner.query(connection, sql, new MapListHandler());
            mapList.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            CupUtil.close(connection);
        }
    }

    /**
     * 修改数据
     */
    public static void update() {
        //创建QueryRunner 对象
        QueryRunner queryRunner = new QueryRunner();
        //获取Connection连接
        Connection connection = CupUtil.getConnection();
        //定义sql语句
        String sql = "update person set name = ?, age = ? where id = ?;";
        try {
            assert connection != null;
            queryRunner.update(connection, sql, "zhangsan", 30, 1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            CupUtil.close(connection);
        }
    }

    /**
     * 删除记录
     */
    public static void delete() {
        //创建QueryRunner 对象
        QueryRunner queryRunner = new QueryRunner();
        //获取Connection连接
        Connection connection = CupUtil.getConnection();
        //定义sql语句
        String sql = "delete from person where id=?";
        try {
            assert connection != null;
            queryRunner.update(connection, sql, 1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            CupUtil.close(connection);
        }
    }

    /**
     * 添加一条记录
     */
    public static void insertOne() {
        //创建QueryRunner 对象
        QueryRunner queryRunner = new QueryRunner();
        //获取Connection连接
        Connection connection = CupUtil.getConnection();
        //定义sql语句
        String sql = "insert into person(name,age) values(?,?)";
        try {
            assert connection != null;
            int update = queryRunner.update(connection, sql, "zhangsan", 20);
            System.out.println(update);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            CupUtil.close(connection);
        }
    }

    /**
     * 添加多条记录
     */
    public static void insertBach() {
        //创建QueryRunner 对象
        QueryRunner queryRunner = new QueryRunner();
        //获取Connection连接
        Connection connection = CupUtil.getConnection();
        //定义sql语句
        String sql = "insert into person(name,age) values(?,?)";
        try {
            assert connection != null;
            Object[][] list = new Object[5][2];
            list[0] = new Object[]{"zhangsan", 20};
            list[1] = new Object[]{"zhangsan", 21};
            list[2] = new Object[]{"zhangsan", 22};
            list[3] = new Object[]{"zhangsan", 23};
            list[4] = new Object[]{"zhangsan", 24};
            queryRunner.batch(connection, sql, list);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            CupUtil.close(connection);
        }
    }

}