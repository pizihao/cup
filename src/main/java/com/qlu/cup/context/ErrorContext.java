package com.qlu.cup.context;

/**
 * @program: cup
 * @description: 错误上下文环境
 * @author: liuwenhao
 * @create: 2021-01-25 14:36
 **/
public class ErrorContext {

    /**
     * 获得 \n 不同的操作系统不一样
     */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    /**
     * 每个线程给开一个错误上下文，防止多线程问题
     */
    private static final ThreadLocal<ErrorContext> LOCAL = new ThreadLocal<ErrorContext>();

    private ErrorContext stored;
    private String resource;
    private String activity;
    private String object;
    private String message;
    private String sql;
    private Throwable cause;

    //单例模式
    private ErrorContext() {
    }

    //工厂方法，得到一个实例
    public static ErrorContext instance() {
        //因为是多线程，所以用了ThreadLocal  线程安全
        ErrorContext context = LOCAL.get();
        //懒汉 单例模式
        if (context == null) {
            context = new ErrorContext();
            LOCAL.set(context);
        }
        return context;
    }

    //啥意思？把ErrorContext存起来供后用？并把ThreadLocal里的东西清空了？
    public ErrorContext store() {
        stored = this;
        LOCAL.set(new ErrorContext());
        return LOCAL.get();
    }

    //应该是和store相对应的方法，store是存储起来，recall是召回
    public ErrorContext recall() {
        if (stored != null) {
            LOCAL.set(stored);
            stored = null;
        }
        return LOCAL.get();
    }

    //以下都是建造者模式
    public ErrorContext resource(String resource) {
        this.resource = resource;
        return this;
    }

    public ErrorContext activity(String activity) {
        this.activity = activity;
        return this;
    }

    public ErrorContext object(String object) {
        this.object = object;
        return this;
    }

    public ErrorContext message(String message) {
        this.message = message;
        return this;
    }

    public ErrorContext sql(String sql) {
        this.sql = sql;
        return this;
    }

    public ErrorContext cause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    //全部清空重置
    public ErrorContext reset() {
        resource = null;
        activity = null;
        object = null;
        message = null;
        sql = null;
        cause = null;
        LOCAL.remove();
        return this;
    }

    //打印上下文信息
    @Override
    public String toString() {
        StringBuilder description = new StringBuilder();

        if (this.message != null) {
            description.append(LINE_SEPARATOR);
            description.append("### ");
            description.append(this.message);
        }

        if (resource != null) {
            description.append(LINE_SEPARATOR);
            description.append("### The error may exist in ");
            description.append(resource);
        }

        if (object != null) {
            description.append(LINE_SEPARATOR);
            description.append("### The error may involve ");
            description.append(object);
        }

        if (activity != null) {
            description.append(LINE_SEPARATOR);
            description.append("### The error occurred while ");
            description.append(activity);
        }

        if (sql != null) {
            description.append(LINE_SEPARATOR);
            description.append("### SQL: ");
            //把sql压缩到一行里
            description.append(sql.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim());
        }

        if (cause != null) {
            description.append(LINE_SEPARATOR);
            description.append("### Cause: ");
            description.append(cause.toString());
        }

        return description.toString();
    }

}