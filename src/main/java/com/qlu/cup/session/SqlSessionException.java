package com.qlu.cup.session;

import com.qlu.cup.exception.CupException;

/**
 * SqlSession异常
 * @author liuwenhao
 */
public class SqlSessionException extends CupException {

  private static final long serialVersionUID = 3833184690240265047L;

  public SqlSessionException() {
    super();
  }

  public SqlSessionException(String message) {
    super(message);
  }

  public SqlSessionException(String message, Throwable cause) {
    super(message, cause);
  }

  public SqlSessionException(Throwable cause) {
    super(cause);
  }
}
