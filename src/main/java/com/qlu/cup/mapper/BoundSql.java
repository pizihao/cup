package com.qlu.cup.mapper;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.bind.Environment;
import com.qlu.cup.parameter.ParameterMapping;
import com.qlu.cup.result.ResultType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @program: cup
 * @description: 映射文件和接口方法的对应类
 * @author: liuwenhao
 * @create: 2021-03-03 14:17
 **/
@Data
@Builder(toBuilder = true)
public class BoundSql {
    /**
     * 命名空间
     **/
    private String namespace;
    /**
     * 命名空间+接口方法名
     **/
    private String nameId;
    /**
     * 对应接口方法名
     **/
    private String name;
    /**
     * 原始sql
     **/
    private String sql;
    private String parameter;
    private String result;
    /**
     * 参数映射
     **/
    private Map<String, ParameterMapping> parameterMap;

    /**
     * 参数名+参数位置
     **/
    private Map<String, Integer> parameterIndex;
    /**
     * 结果映射
     **/
    private ResultType resultType;
    /**
     * 具体的操作 如select
     **/
    private String handle;
    /**
     * 当前环境
     **/
    private Environment environment;
    /**
     * 全局配置
     **/
    private Configuration configuration;

}