package com.qlu.cup.type;

import com.qlu.cup.exception.CupException;

/**
 * 类型异常
 */
public class TypeException extends CupException {

    private static final long serialVersionUID = 8614420898975117130L;

    public TypeException() {
        super();
    }

    public TypeException(String message) {
        super(message);
    }

    public TypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeException(Throwable cause) {
        super(cause);
    }

}
