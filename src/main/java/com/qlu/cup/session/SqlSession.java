package com.qlu.cup.session;

import com.qlu.cup.result.ResultProcessor;

import java.io.Closeable;

/**
 * @program: cup
 * @description: 会话
 * @author: liuwenhao
 * @create: 2021-01-23 17:52
 **/
public interface SqlSession extends Closeable {

    /**
     * @description: 传入一个要执行的sql和参数，并通过结果处理器进行处理
     * @param statement sql执行声明
     * @param processor 结果处理器
     * @param parameter 参数
     * @return void
     * @author liuwenaho
     * @date 2021/1/23 18:33
     */
    void select(String statement, Object parameter, ResultProcessor processor);
}