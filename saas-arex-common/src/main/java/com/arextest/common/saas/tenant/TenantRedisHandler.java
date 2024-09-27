package com.arextest.common.saas.tenant;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.saas.utils.RedisKeyBuilder;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TenantRedisHandler {

  private static final long TENANT_STATUS_EXPIRE_SECONDS = 2 * 60 * 60;

  private final CacheProvider cacheProvider;

  private final ObjectMapper objectMapper;

  public boolean saveTenantStatus(String tenantCode, TenantStatusRedisInfo tenantStatusRedisInfo) {
    try {
      byte[] key = RedisKeyBuilder.buildCommonTenantStatusKey(tenantCode);
      byte[] value = buildTenantStatusValue(tenantStatusRedisInfo);
      cacheProvider.put(key, value);
    } catch (Exception e) {
      LOGGER.error("saveTenantStatus error, tenantCode:{}, exception:{}", tenantCode,
          e.getMessage());
      return false;
    }
    return true;
  }

  public boolean saveTenantStatusExpire(String tenantCode, TenantStatusRedisInfo tenantStatusRedisInfo) {
    try {
      byte[] key = RedisKeyBuilder.buildCommonTenantStatusKey(tenantCode);
      byte[] value = buildTenantStatusValue(tenantStatusRedisInfo);
      cacheProvider.putIfAbsent(key, TENANT_STATUS_EXPIRE_SECONDS, value);
    } catch (Exception e) {
      LOGGER.error("saveTenantStatusExpire error, tenantCode:{}, exception:{}", tenantCode,
          e.getMessage());
      return false;
    }
    return true;
  }


  public TenantStatusRedisInfo getTenantStatus(String tenantCode) {
    try {
      byte[] key = RedisKeyBuilder.buildCommonTenantStatusKey(tenantCode);
      byte[] value = cacheProvider.get(key);
      if (value != null) {
        return parseTenantStatusValue(value);
      }
    } catch (Exception e) {
      LOGGER.error("getTenantStatus error, tenantCode:{}, exception:{}", tenantCode,
          e.getMessage());
    }
    return null;
  }

  public boolean existTenant(String tenantCode) {
    try {
      byte[] key = RedisKeyBuilder.buildCommonTenantStatusKey(tenantCode);
      return cacheProvider.get(key) != null;
    } catch (Exception e) {
      LOGGER.error("existTenantStatus error, tenantCode:{}, exception:{}", tenantCode,
          e.getMessage());
      return false;
    }
  }

  public boolean removeTenant(String tenantCode) {
    try {
      byte[] key = RedisKeyBuilder.buildCommonTenantStatusKey(tenantCode);
      cacheProvider.remove(key);
    } catch (Exception e) {
      LOGGER.error("removeTenantStatus error, tenantCode:{}, exception:{}", tenantCode,
          e.getMessage());
      return false;
    }
    return true;
  }

  private byte[] buildTenantStatusValue(TenantStatusRedisInfo tenantStatusRedisInfo)
      throws JacksonException {
    return objectMapper.writeValueAsString(tenantStatusRedisInfo)
        .getBytes(StandardCharsets.UTF_8);
  }

  public TenantStatusRedisInfo parseTenantStatusValue(byte[] value) throws IOException {
    return objectMapper.readValue(value, TenantStatusRedisInfo.class);
  }


}
