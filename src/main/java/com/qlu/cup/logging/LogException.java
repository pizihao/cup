package com.qlu.cup.logging;

import com.qlu.cup.exception.CupException;

/**
 * 日志异常,继承RuntimeException，语义分类
 */
public class LogException extends CupException {

  private static final long serialVersionUID = 1022924004852350942L;

  public LogException() {
    super();
  }

  public LogException(String message) {
    super(message);
  }

  public LogException(String message, Throwable cause) {
    super(message, cause);
  }

  public LogException(Throwable cause) {
    super(cause);
  }

}
