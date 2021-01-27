package com.qlu.cup.transaction;

import com.qlu.cup.exception.CupException;

/**
 * 事务异常,继承RuntimeException
 */
public class TransactionException extends CupException {

  private static final long serialVersionUID = -433589569461084605L;

  public TransactionException() {
    super();
  }

  public TransactionException(String message) {
    super(message);
  }

  public TransactionException(String message, Throwable cause) {
    super(message, cause);
  }

  public TransactionException(Throwable cause) {
    super(cause);
  }

}
