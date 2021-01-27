package com.qlu.cup.exception;


import com.qlu.cup.context.ErrorContext;

/**
 * 异常工厂
 */
public class ExceptionFactory {

  private ExceptionFactory() {}

  public static RuntimeException wrapException(String message, Exception e) {
    return new CupException(ErrorContext.instance().message(message).cause(e).toString(), e);
  }

}
