package com.qlu.cup.builder.yml;

import com.qlu.cup.exception.CupException;

/**
 * @program: cup
 * @description: 映射文件异常
 * @author: liuwenhao
 * @create: 2021-01-27 11:06
 **/
public class MapperException extends CupException {

    private static final long serialVersionUID = -7537395265357977271L;

    public MapperException() {
        super();
    }

    public MapperException(String message) {
        super(message);
    }

    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperException(Throwable cause) {
        super(cause);
    }
}