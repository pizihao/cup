package com.qlu.cup.bind;

import com.qlu.cup.exception.CupException;

/**
 * @program: cup
 * @description: 映射绑定异常
 * @author: liuwenhao
 * @create: 2021-01-28 11:41
 **/
public class BindException extends CupException {
    private static final long serialVersionUID = -7537395265357977271L;

    public BindException() {
        super();
    }

    public BindException(String message) {
        super(message);
    }

    public BindException(String message, Throwable cause) {
        super(message, cause);
    }

    public BindException(Throwable cause) {
        super(cause);
    }
}