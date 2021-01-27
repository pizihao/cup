package com.qlu.cup.exception;

/**
 * 持久化异常
 */
@SuppressWarnings("deprecation")
public class CupException extends RuntimeException {

  private static final long serialVersionUID = -7537395265357977271L;

  public CupException() {
    super();
  }

  public CupException(String message) {
    super(message);
  }

  public CupException(String message, Throwable cause) {
    super(message, cause);
  }

  public CupException(Throwable cause) {
    super(cause);
  }
}
