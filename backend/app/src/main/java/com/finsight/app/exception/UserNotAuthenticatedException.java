package com.finsight.app.exception;

public class UserNotAuthenticatedException extends RuntimeException {
  public UserNotAuthenticatedException(String message) {
    super(message);
  }
}
