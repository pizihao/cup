package com.qlu.cup.mapper;

import com.qlu.cup.executor.ExecutorException;
import com.qlu.cup.statement.ParameterHandler;

/**
 * SQL语句的形式
 *
 * @program: cup
 * @author: liuwenhao
 * @create: 2021-05-29 14:04
 **/
public enum SqlType {
    /**
     * 数据操作语言
     */
    DML("DML", (statement, handler) -> {
        handler.parameterize(statement);
    }),
    /**
     * 数据控制语言
     */
    DCL("DCL", (statement, handler) -> {
        throw new ExecutorException("不支持的sql类型:DCL");
    }),
    /**
     * 数据定义语言
     */
    DDL("DDL", (statement, handler) -> {
        handler.parameterizeToDML(statement);
    });

    String name;
    ParameterHandler parameterHandler;

    public String getName() {
        return name;
    }

    public ParameterHandler getParameterHandler() {
        return parameterHandler;
    }

    SqlType(String name, ParameterHandler parameterHandler) {
        this.name = name;
        this.parameterHandler = parameterHandler;
    }
}
