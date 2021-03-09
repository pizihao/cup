package com.qlu.cup.result;

import lombok.Builder;
import lombok.Data;

/**
 * @program: cup
 * @description: 结果映射
 * @author: liuwenhao
 * @create: 2021-03-03 14:51
 **/
@Data
@Builder(toBuilder = true)
public class ResultMapping {
    /**
     * 返回值名字，对应属性名或者基本数据类型的字符串等等
     **/
    private String name;

    /**
     * 返回值类型
     **/
    private Class<?> type;

    /**
     * 实体类名
     **/
    private String className;

}