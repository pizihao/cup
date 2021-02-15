package com.qlu.cup.reflection;

import com.qlu.cup.exception.CupException;

/**
 * 反射异常
 */
public class ReflectionException extends CupException {

  private static final long serialVersionUID = 7642570221267566591L;

  public ReflectionException() {
    super();
  }

  public ReflectionException(String message) {
    super(message);
  }

  public ReflectionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReflectionException(Throwable cause) {
    super(cause);
  }

}
