package com.qlu.cup.factory;

import com.qlu.cup.session.SqlSession;

/**
 * @program: cup
 * @description: 创建一个sql会话的工厂
 * @author: liuwenhao
 * @create: 2021-01-23 17:48
 **/
public interface SqlSessionFactory {
    SqlSession creatSession();
}