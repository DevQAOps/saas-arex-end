package com.arextest.common.saas.enums;

/**
 * 9(first)-xx(second)-xxx(third)-00(forth) The first represents the service, 9 for common error, 1
 * for api error, 2 for scheduler error, 3 for storage error. The second represents the function.
 * The third represents the error code. The forth is the fixed value.
 */
public enum SaasErrorCode {

  // common error, 9-xx-xxx-00
  SAAS_COMMON_ERROR(90000000, "system error"),
  SAAS_TENANT_NOT_FOUND(90000100, "the tenant is not found"),
  SAAS_TENANT_EXPIRED(90000200, "the tenant is expired"),
  // api error 1-xx-xxx-00
  SAAS_USER_NOT_FOUND(10000100, "the user is not found")

  // scheduler error 2-xx-xxx-00

  // storage error 3-xx-xxx-00

  ;

  private final int codeValue;

  private final String message;

  SaasErrorCode(int codeValue, String message) {
    this.codeValue = codeValue;
    this.message = message;
  }

  public int getCodeValue() {
    return codeValue;
  }

  public String getMessage() {
    return message;
  }
}
