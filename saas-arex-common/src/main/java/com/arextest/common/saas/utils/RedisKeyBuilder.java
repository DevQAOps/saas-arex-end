package com.arextest.common.saas.utils;

import java.nio.charset.StandardCharsets;

public class RedisKeyBuilder {

  /**
   * the first %s is the service, the second %s is the function, the third %s is the key
   */
  private static final String KEY_TEMPLATE = "%s:%s:%s";

  private static final byte[] EMPTY_BYTE = new byte[]{};

  private static byte[] toUtf8Bytes(String value) {
    return value == null ? EMPTY_BYTE : value.getBytes(StandardCharsets.UTF_8);
  }

  public static byte[] buildCommonTenantStatusKey(String tenantCode) {
    String format = String.format(KEY_TEMPLATE, SERVICE_SUMMARY.SAAS_COMMON,
        FUNCTION_SUMMARY.TENANT_STATUS, tenantCode);
    return toUtf8Bytes(format);
  }

  /**
   * the summary of service
   */
  private interface SERVICE_SUMMARY {

    String SAAS_COMMON = "saas.common";

    String SAAS_API = "saas.api";

    String SAAS_SCHEDULE = "saas.schedule";

    String SAAS_STORAGE = "saas.storage";

    String SAAS_DEVOPS = "saas.devops";
  }

  /**
   * the summary of function
   */
  private interface FUNCTION_SUMMARY {

    String TENANT_STATUS = "tenant.status";
  }


}
