package com.qlu.cup.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @program: cup
 * @description: 事务接口
 * @author: liuwenhao
 * @create: 2021-01-23 20:02
 **/
public interface Transaction {

    Connection getConnection() throws SQLException;

    /**
     * @description: 提交事务
     * @author liuwenaho
     * @date 2021/1/23 20:03
     */
    void commit() throws SQLException;

    /**
     * @description: 回滚事务
     * @author liuwenaho
     * @date 2021/1/23 20:03
     */
    void rollback() throws SQLException;

    /**
     * @description: 关闭
     * @author liuwenaho
     * @date 2021/1/23 20:03
     */
    void close() throws SQLException;

}
