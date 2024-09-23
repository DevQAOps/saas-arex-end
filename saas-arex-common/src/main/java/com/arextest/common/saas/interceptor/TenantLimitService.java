package com.arextest.common.saas.interceptor;

import com.arextest.common.saas.enums.SaasErrorCode;
import com.arextest.common.saas.tenant.TenantRedisHandler;
import com.arextest.common.saas.tenant.TenantStatusRedisInfo;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class TenantLimitService {

  private final TenantRedisHandler tenantRedisHandler;

  private final TenantStatusProvider tenantStatusProvider;

  public TenantLimitResult limitTenant(TenantLimitInfo tenantLimitInfo) throws RuntimeException {

    TenantStatusRedisInfo tenantStatus = tenantRedisHandler.getTenantStatus(
        tenantLimitInfo.getTenantCode());

    // if redis not found, query from saasDb by arex-saas-service
    if (tenantStatus == null) {
      tenantStatus = tenantStatusProvider.fetchTenantStatus(tenantLimitInfo.getTenantCode());
    }

    // save to redis
    if (tenantStatus == null) {
      tenantStatus = new TenantStatusRedisInfo();
      tenantRedisHandler.saveTenantStatusExpire(tenantLimitInfo.getTenantCode(), tenantStatus);
    } else {
      tenantRedisHandler.saveTenantStatus(tenantLimitInfo.getTenantCode(), tenantStatus);
    }

    // verify whether tenant exists
    if (StringUtils.isEmpty(tenantStatus.getTenantToken())) {
      return TenantLimitResult.builder().pass(Boolean.FALSE)
          .errorCode(SaasErrorCode.SAAS_TENANT_NOT_FOUND).build();
    }

    // verify whether the tenant has expired
    if (tenantStatus.getExpireTime() < System.currentTimeMillis()) {
      return TenantLimitResult.builder().pass(Boolean.FALSE)
          .errorCode(SaasErrorCode.SAAS_TENANT_EXPIRED).build();
    }
    return TenantLimitResult.builder().pass(Boolean.TRUE).build();
  }

  @Data
  @Builder
  static class TenantLimitInfo {

    private String tenantCode;
  }

  @Data
  @Builder
  static class TenantLimitResult {

    private boolean pass;
    private SaasErrorCode errorCode;
  }

}