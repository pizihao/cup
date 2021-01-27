package com.qlu.cup.conf;

import com.qlu.cup.exception.CupException;

/**
 * @program: cup
 * @description: 配置异常
 * @author: liuwenhao
 * @create: 2021-01-26 18:36
 **/
public class ConfException extends CupException {
    private static final long serialVersionUID = -7537395265357977271L;

    public ConfException() {
        super();
    }

    public ConfException(String message) {
        super(message);
    }

    public ConfException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfException(Throwable cause) {
        super(cause);
    }
}