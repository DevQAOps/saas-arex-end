package com.arextest.saas.api.common.exceptions;

/**
 * @author wildeslam.
 * @create 2024/3/21 15:31
 */
public class ArexSaasException extends RuntimeException {

  private int code;

  public ArexSaasException(int code, String message) {
    super(message);
    this.code = code;
  }

  public ArexSaasException(int code, Throwable cause) {
    super(cause);
    this.code = code;
  }

  public ArexSaasException(int code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
