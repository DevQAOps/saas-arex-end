package com.arextest.saas.api.common.enums;

import lombok.Getter;

/**
 * @author wildeslam.
 * @create 2024/3/21 15:34
 */
public enum ErrorCode {
  SUCCESS(0),

  // login error starts with 1xxx
  LOGIN_FAILED(1000),
  TENANT_EXISTED(1001),
  TENANT_NOT_EXISTED(1002),
  SEND_EMAIL_FAILED(1003),
  VERIFY_FAILED(1004),
  PASSWORD_ERROR(1005),
  USER_BOUND(1006),
  TENANT_NOT_BOUND(1007),
  COMPANY_CODE_ERROR(1008),
  COMPANY_CODE_BOUND(1009),
  COMPANY_TOKEN_ERROR(1010),
  INIT_USER_ERROR(1011),
  PACKAGE_NOT_EXISTED(1012),
  EMAIL_FORMAT_ERROR(1013),
  MEMBER_EXCEED_LIMIT(1014),
  EMPTY_TENANT_CODE(1015),
  UPN_FORMAT_ERROR(1016),
  CONVERT_VERIFICATION_INFO_ERROR(1017),
  OAUTH_EMPTY_EMAIL_EXCEPTION(1018),
  USER_ALREADY_EXISTED(1019),
  OAUTH_RESULT_GET_EXCEPTION(1020),

  // client login starts with 21xx
  CLIENT_LOGIN_PROVIDER_NOT_SUPPORTED(2100),
  CLIENT_LOGIN_AUTH0_VERIFY_FAILED(2101),
  CLIENT_LOGIN_USER_NOT_FOUND(2102),
  CLIENT_LOGIN_EMAIL_NOT_VERIFIED(2103)

  ;

  @Getter
  private final int codeValue;


  ErrorCode(int codeValue) {
    this.codeValue = codeValue;
  }
}
