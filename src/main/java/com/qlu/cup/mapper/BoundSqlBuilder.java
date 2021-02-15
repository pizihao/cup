package com.qlu.cup.mapper;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.builder.StaticSqlSource;
import com.qlu.cup.builder.yml.YNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: cup
 * @description: 创建BoundSql
 * @author: liuwenhao
 * @create: 2021-01-28 22:24
 **/
public class BoundSqlBuilder {

    /**
     * @param nodeMap key为类全限定名，value为YNode对象的map集合
     * @return java.util.Map<java.lang.String, com.qlu.cup.mapper.BoundSql>
     * @description: 创建一个新的映射关系，将映射关系从接口-->映射文件细粒化为接口方法-->映射文件节点
     * @author liuwenaho
     * @date 2021/1/29 20:26
     */
    public static Map<String, BoundSql> builder(Map<String, YNode> nodeMap, Configuration configuration) {
        Map<String, BoundSql> hashMap = new HashMap<>(16);
        nodeMap.forEach((aClass, yNode) -> {
            String classId = yNode.getNamespace() + "." + yNode.getName();
            hashMap.put(classId, new BoundSql(yNode.getId(), yNode.getSql()
                    , yNode.getParameterType(), yNode.getResultType(), classId, configuration.getEnvironment()
                    , new StaticSqlSource(configuration,yNode.getSql())));
        });
        return hashMap;
    }
}