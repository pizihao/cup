package com.qlu.cup.mapper;

import com.qlu.cup.bind.BindException;
import com.qlu.cup.bind.Configuration;
import com.qlu.cup.builder.yml.YNode;

import java.lang.reflect.Method;

/**
 * @program: cup
 * @description: sql命令
 * @author: liuwenhao
 * @create: 2021-01-28 17:22
 **/
public class SqlCommand {
    /**
     * 加上命名空间的方法名
     */
    private String name;
    /**
     * 数据库操作类型，如select
     */
    private String type;

    public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
        String statementName = mapperInterface.getName() + "." + method.getName();
        BoundSql boundSql = null;
        if (configuration.hasNode(mapperInterface.getName(),statementName)) {
            boundSql = configuration.getMappedYnode(statementName);
        } else if (!mapperInterface.equals(method.getDeclaringClass().getName())) {
            //如果不是这个mapper接口的方法，再去查父类
            String parentStatementName = method.getDeclaringClass().getName() + "." + method.getName();
            if (configuration.hasNode(mapperInterface.getName(),parentStatementName)) {
                boundSql = configuration.getMappedYnode(parentStatementName);
            }
        }
        if (boundSql == null) {
            throw new BindException("Invalid bound statement (not found): " + statementName);
        }
        name = boundSql.getID();
        type = boundSql.getHandle();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}