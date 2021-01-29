package com.qlu.cup.mapper;

import com.qlu.cup.builder.yml.YNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: cup
 * @description: 创建BoundSql
 * @author: liuwenhao
 * @create: 2021-01-28 22:24
 **/
public class BoundSqlBuilder {

    public static Map<String,BoundSql> builder(Map<String, YNode> nodeMap) {
        Map<String,BoundSql> hashMap = new HashMap<>(16);
        nodeMap.forEach((aClass, yNode) -> {
            hashMap.put(yNode.getNamespace() + yNode.getName(),new BoundSql(yNode.getId(), yNode.getSql()
                    , yNode.getParameterType(), yNode.getResultType(), yNode.getNamespace() + yNode.getName()));
        });
        return hashMap;
    }
}