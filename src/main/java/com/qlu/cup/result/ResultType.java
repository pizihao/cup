package com.qlu.cup.result;

import com.qlu.cup.bind.Configuration;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @program: cup
 * @description: 结果类型
 * @author: liuwenhao
 * @create: 2021-03-03 14:52
 **/
@Data
@Builder(toBuilder = true)
public class ResultType {
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
     * 具体的返回值名字,接口方法的
     **/
    private String resultType;

    /**
     * 属性名和属性类型
     **/
    private Map<String, ResultMapping> resultMap;
}