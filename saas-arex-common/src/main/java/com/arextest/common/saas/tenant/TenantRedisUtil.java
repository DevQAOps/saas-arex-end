package com.arextest.common.saas.tenant;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TenantRedisUtil {

  private static final ObjectMapper objectRedisMapper = new ObjectMapper();

  private static final String TENANT_STATUS_KEY = "arex_tenant_status";

  private static final byte[] EMPTY_BYTE = new byte[]{};

  private static byte[] toUtf8Bytes(String value) {
    return value == null ? EMPTY_BYTE : value.getBytes(StandardCharsets.UTF_8);
  }

  public static byte[] buildTenantStatusKey(String tenantCode) {
    return toUtf8Bytes(TENANT_STATUS_KEY + tenantCode);
  }

  public static byte[] buildTenantStatusValue(TenantStatusRedisInfo tenantStatusRedisInfo)
      throws JacksonException {
    return objectRedisMapper.writeValueAsString(tenantStatusRedisInfo)
        .getBytes(StandardCharsets.UTF_8);
  }

  public static TenantStatusRedisInfo parseTenantStatusValue(byte[] value) throws IOException {
    return objectRedisMapper.readValue(value, TenantStatusRedisInfo.class);
  }
}
