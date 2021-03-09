package com.qlu.cup.parameter;

import com.qlu.cup.bind.Configuration;
import lombok.Builder;
import lombok.Data;

/**
 * @program: cup
 * @description: 参数映射
 * @author: liuwenhao
 * @create: 2021-03-03 14:26
 **/
@Data
@Builder(toBuilder = true)
public class ParameterMapping {
    private Configuration configuration;
    /**
     * 命名空间
     **/
    private String namespace;
    /**
     * 对应接口方法名
     **/
    private String name;
    /**
     * 具体的参数名字
     **/
    private String parameterName;

    /**
     * 参数数据类型，默认是Object
     **/
    private Class<?> javaType = Object.class;
}