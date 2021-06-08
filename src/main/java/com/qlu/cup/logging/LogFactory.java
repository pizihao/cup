package com.qlu.cup.logging;

import com.qlu.cup.logging.log4j.Log4jImpl;
import com.qlu.cup.logging.nologging.NoLoggingImpl;

import java.lang.reflect.Constructor;


/**
 * 日志工厂
 */
public final class LogFactory {

    public static final String MARKER = "MYBATIS";

    private static Constructor<? extends Log> logConstructor;

    static {
        tryImplementation(LogFactory::useLog4JLogging);
        //没有日志
        tryImplementation(LogFactory::useNoLogging);
    }

    //单例模式，不得自己new实例
    private LogFactory() {
        // disable construction
    }

    //根据传入的类来构建Log
    public static Log getLog(Class<?> aClass) {
        return getLog(aClass.getName());
    }

    public static Log getLog(String logger) {
        try {
            //构造函数，参数必须是一个，为String型，指明logger的名称
            return logConstructor.newInstance(logger);
        } catch (Throwable t) {
            throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
        }
    }

    /**
     * 提供一个扩展功能，如果以上log都不满意，可以使用自定义的log
     * @param clazz
     */
    public static synchronized void useCustomLogging(Class<? extends Log> clazz) {
        setImplementation(clazz);
    }

    public static synchronized void useLog4JLogging() {
        setImplementation(Log4jImpl.class);
    }

    public static synchronized void useNoLogging() {
        setImplementation(NoLoggingImpl.class);
    }

    private static void tryImplementation(Runnable runnable) {
        if (logConstructor == null) {
            try {
                //这里调用的不是start,而是run！根本就没用多线程嘛！
                runnable.run();
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    private static void setImplementation(Class<? extends Log> implClass) {
        try {
            Constructor<? extends Log> candidate = implClass.getConstructor(String.class);
            Log log = candidate.newInstance(LogFactory.class.getName());
            //设置logConstructor,一旦设上，表明找到相应的log的jar包了，那后面别的log就不找了。
            logConstructor = candidate;
        } catch (Throwable t) {
            throw new LogException("接口信息异常" + t, t);
        }
    }

}
