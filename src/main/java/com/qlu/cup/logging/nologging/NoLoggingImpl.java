package com.qlu.cup.logging.nologging;

import com.qlu.cup.logging.Log;

/**
 * 不做日志,所以都是空方法
 */
public class NoLoggingImpl implements Log {

    public NoLoggingImpl(Class<?> clazz) {
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void error(String s, Throwable e) {
    }

    @Override
    public void error(String s) {
    }

    @Override
    public void debug(String s) {
    }

    @Override
    public void trace(String s) {
    }

    @Override
    public void warn(String s) {
    }

    @Override
    public void info(String s) {
    }

}
