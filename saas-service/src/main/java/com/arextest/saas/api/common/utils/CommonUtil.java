package com.arextest.saas.api.common.utils;

import com.arextest.saas.api.common.enums.ErrorCode;
import com.arextest.saas.api.common.exceptions.ArexSaasException;
import java.security.SecureRandom;
import org.apache.commons.lang3.StringUtils;

/**
 * @author b_yu
 * @since 2024/7/25
 */
public class CommonUtil {

  private static final SecureRandom random = new SecureRandom();
  private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

  public static String generateVerificationCode() {
    StringBuilder verificationCode = new StringBuilder(6);
    for (int i = 0; i < 6; i++) {
      verificationCode.append(random.nextInt(10));
    }
    return verificationCode.toString();
  }

  public static void checkTenantCode(String tenantCode) {
    if (StringUtils.isEmpty(tenantCode)) {
      throw new ArexSaasException(ErrorCode.EMPTY_TENANT_CODE.getCodeValue(), "Tenant is empty");
    }
  }

  public static boolean validateEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      return false;
    }
    return email.matches(EMAIL_REGEX);
  }
}
