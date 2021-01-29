package com.qlu.cup.mapper;

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
        this.parameterType = ObjectInterface.getClazz(parameterType);
        this.resultType = ObjectInterface.getClazz(resultType);
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

}
