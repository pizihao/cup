package com.qlu.cup.executor;

import com.qlu.cup.exception.CupException;

/**
 * @program: cup
 * @description: 执行器异常
 * @author: liuwenhao
 * @create: 2021-02-10 22:42
 **/
public class ExecutorException extends CupException {
    private static final long serialVersionUID = -7537395265357977271L;

    public ExecutorException() {
        super();
    }

    public ExecutorException(String message) {
        super(message);
    }

    public ExecutorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutorException(Throwable cause) {
        super(cause);
    }
}