package com.qlu.cup.mapper;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.builder.yml.MapperException;

import java.util.*;

/**
 * SQL，参数和命名空间+id进行绑定
 */
public class BoundSql {

    /**
     * 对应的操作
     */
    private String handle;

    /**
     * 未经过处理的sql
     */
    private String sql;

    /**
     * 参数类型
     */
    private Class<?> parameterType;

    /**
     * 返回值类型
     */
    private Class<?> resultType;

    /**
     * 命名空间+id
     */
    private String ID;

    public BoundSql(String handle,String sql,String parameterType,String resultType,String ID) {
        this.handle = handle;
        this.ID = ID;
        this.parameterType = getClazz(parameterType);
        this.resultType = getClazz(resultType);
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setParameterType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public static Class<?> getClazz(String str) {
        if (str == null || "".equals(str)){
            return null;
        }
        Class<?> classToCreate = null;
        switch (str){
            case "List":
            case "Collection":
            case "Iterable":
                classToCreate = ArrayList.class;
                break;
            case "Map":
                classToCreate = HashMap.class;
                break;
            case "SortedSet":
                classToCreate = TreeSet.class;
                break;
            case "Set":
                classToCreate = HashSet.class;
                break;
            case "int":
            case "Integer":
                classToCreate = int.class;
                break;
            case "double":
            case "Double":
                classToCreate = double.class;
                break;
            case "float":
            case "Float":
                classToCreate = float.class;
                break;
            case "long":
            case "Long":
                classToCreate = long.class;
                break;
            case "char":
            case "Character":
                classToCreate = char.class;
                break;
            case "short":
            case "Short":
                classToCreate = short.class;
                break;
            case "boolean":
            case "Boolean":
                classToCreate = boolean.class;
                break;
            case "void":
            case "Void":
                classToCreate = void.class;
                break;
            case "String":
                classToCreate = String.class;
                break;
            default:
                try {
                    classToCreate = Class.forName(str);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
        return classToCreate;
    }
}
