package com.qlu.cup.builder;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.mapper.ObjectInterface;
import com.qlu.cup.type.JdbcType;
import com.qlu.cup.type.TypeHandler;
import com.qlu.cup.type.TypeHandlerRegistry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 构建器的基类，建造者模式
 */
public abstract class BaseBuilder {
    //需要配置，类型处理器注册
    protected final Configuration configuration;
    protected final TypeHandlerRegistry typeHandlerRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeHandlerRegistry = this.configuration.getTypeHandlerRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    protected Pattern parseExpression(String regex, String defaultValue) {
        return Pattern.compile(regex == null ? defaultValue : regex);
    }

    protected Boolean booleanValueOf(String value, Boolean defaultValue) {
        return value == null ? defaultValue : Boolean.valueOf(value);
    }

    protected Integer integerValueOf(String value, Integer defaultValue) {
        return value == null ? defaultValue : Integer.valueOf(value);
    }

    //把以逗号分割的一个字符串重新包装，返回一个Set
    protected Set<String> stringSetValueOf(String value, String defaultValue) {
        value = (value == null ? defaultValue : value);
        return new HashSet<String>(Arrays.asList(value.split(",")));
    }

    //解析JdbcType
    protected JdbcType resolveJdbcType(String type) {
        if (type == null) {
            return null;
        }
        try {
            return JdbcType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new BuilderException("Error resolving JdbcType. Cause: " + e, e);
        }
    }

    //根据别名解析Class，然后创建实例
    protected Object createInstance(String type) {
        Class<?> clazz = resolveClass(type);
        if (clazz == null) {
            return null;
        }
        try {
            return resolveClass(type).newInstance();
        } catch (Exception e) {
            throw new BuilderException("Error creating instance. Cause: " + e, e);
        }
    }

    protected Class<?> resolveClass(String type) {
        if (type == null) {
            return null;
        }
        try {
            return ObjectInterface.getClazz(type);
        } catch (Exception e) {
            throw new BuilderException("Error resolving class. Cause: " + e, e);
        }
    }

    //解析类型处理器
    protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, String typeHandlertype) {
        if (typeHandlertype == null) {
            return null;
        }
        //先取得别名所属的Class
        Class<?> type = resolveClass(typeHandlertype);
        //如果不是TypeHandler的子类,报错
        if (type != null && !TypeHandler.class.isAssignableFrom(type)) {
            throw new BuilderException("Type " + type.getName() + " is not a valid TypeHandler because it does not implement TypeHandler interface");
        }
        @SuppressWarnings("unchecked") // already verified it is a TypeHandler
        Class<? extends TypeHandler<?>> typeHandlerType = (Class<? extends TypeHandler<?>>) type;
        //再去调用另一个重载的方法
        return resolveTypeHandler(javaType, typeHandlerType);
    }

    protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerType) {
        if (typeHandlerType == null) {
            return null;
        }
        //去typeHandlerRegistry查询对应的TypeHandler
        TypeHandler<?> handler = typeHandlerRegistry.getMappingTypeHandler(typeHandlerType);
        if (handler == null) {
            //如果没有在Registry找到，调用typeHandlerRegistry.getInstance来new一个TypeHandler返回
            handler = typeHandlerRegistry.getInstance(javaType, typeHandlerType);
        }
        return handler;
    }

}
