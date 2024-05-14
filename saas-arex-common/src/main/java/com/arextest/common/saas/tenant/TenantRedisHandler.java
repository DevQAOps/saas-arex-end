package com.arextest.common.saas.tenant;

import com.arextest.common.cache.CacheProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TenantRedisHandler {

  private final CacheProvider cacheProvider;

  public boolean saveTenantStatus(String tenantCode, TenantStatusRedisInfo tenantStatusRedisInfo) {
    try {
      byte[] key = TenantRedisUtil.buildTenantStatusKey(tenantCode);
      byte[] value = TenantRedisUtil.buildTenantStatusValue(tenantStatusRedisInfo);
      cacheProvider.put(key, value);
    } catch (Exception e) {
      LOGGER.error("saveTenantStatus error, tenantCode:{}, exception:{}", tenantCode, e.getMessage());
      return false;
    }
    return true;
  }

  public TenantStatusRedisInfo getTenantStatus(String tenantCode) {
    try {
      byte[] key = TenantRedisUtil.buildTenantStatusKey(tenantCode);
      byte[] value = cacheProvider.get(key);
      if (value != null) {
        return TenantRedisUtil.parseTenantStatusValue(value);
      }
    } catch (Exception e) {
      LOGGER.error("getTenantStatus error, tenantCode:{}, exception:{}", tenantCode, e.getMessage());
    }
    return null;
  }

  public boolean existTenant(String tenantCode) {
    try {
      byte[] key = TenantRedisUtil.buildTenantStatusKey(tenantCode);
      return cacheProvider.get(key) != null;
    } catch (Exception e) {
      LOGGER.error("existTenantStatus error, tenantCode:{}, exception:{}", tenantCode, e.getMessage());
      return false;
    }
  }
}
