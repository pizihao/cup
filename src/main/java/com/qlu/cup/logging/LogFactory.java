package com.qlu.cup.logging;

import com.qlu.cup.logging.log4j.Log4jImpl;
import com.qlu.cup.logging.nologging.NoLoggingImpl;

import java.lang.reflect.Constructor;


/**
 * 日志工厂
 */
public final class LogFactory {

  //给支持marker功能的logger使用，配合slf4j
  public static final String MARKER = "MYBATIS";

  //具体究竟用哪个日志框架，那个框架所对应logger的构造函数
  private static Constructor<? extends Log> logConstructor;

  static {
    //这边乍一看以为开了几个并行的线程去决定使用哪个具体框架的logging，其实不然
    //log4j
    tryImplementation(new Runnable() {
      @Override
      public void run() {
        useLog4JLogging();
      }
    });
    //没有日志
    tryImplementation(new Runnable() {
      @Override
      public void run() {
        useNoLogging();
      }
    });
  }

  //单例模式，不得自己new实例
  private LogFactory() {
    // disable construction
  }

  //根据传入的类来构建Log
  public static Log getLog(Class<?> aClass) {
    return getLog(aClass.getName());
  }

  //根据传入的类名来构建Log
  public static Log getLog(String logger) {
    try {
      //构造函数，参数必须是一个，为String型，指明logger的名称
      return logConstructor.newInstance(new Object[] { logger });
    } catch (Throwable t) {
      throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
    }
  }

  //提供一个扩展功能，如果以上log都不满意，可以使用自定义的log
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
      Constructor<? extends Log> candidate = implClass.getConstructor(new Class[] { String.class });
      Log log = candidate.newInstance(new Object[] { LogFactory.class.getName() });
      log.debug("Logging initialized using '" + implClass + "' adapter.");
      //设置logConstructor,一旦设上，表明找到相应的log的jar包了，那后面别的log就不找了。
      logConstructor = candidate;
    } catch (Throwable t) {
      throw new LogException("Error setting Log implementation.  Cause: " + t, t);
    }
  }

}