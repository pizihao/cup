package com.qlu.cup.logging;


/**
 * 日志接口
 */
public interface Log {

    //和一般的log4j很像，提供日志接口的一些方法,error, debug, warn。
    boolean isDebugEnabled();

    boolean isTraceEnabled();

    void error(String s, Throwable e);

    void error(String s);

    void debug(String s);

    void trace(String s);

    void warn(String s);

}
