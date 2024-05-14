package com.arextest.common.saas.interceptor;

import com.arextest.common.saas.enums.SaasErrorCode;
import com.arextest.common.saas.enums.TenantStatus;
import com.arextest.common.saas.tenant.TenantRedisHandler;
import com.arextest.common.saas.tenant.TenantStatusRedisInfo;
import java.util.Objects;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TenantLimitService {

  private final TenantRedisHandler tenantRedisHandler;

  public TenantLimitResult limitTenant(TenantLimitInfo tenantLimitInfo) throws RuntimeException {

    TenantStatusRedisInfo tenantStatus = tenantRedisHandler.getTenantStatus(
        tenantLimitInfo.getTenantCode());

    // verify whether tenant exists
    if (tenantStatus == null) {
      return TenantLimitResult.builder().pass(Boolean.FALSE)
          .errorCode(SaasErrorCode.SAAS_TENANT_NOT_FOUND).build();
    }

    // verify whether the tenant has expired
    if (Objects.equals(tenantStatus.getTenantStatus(), TenantStatus.INACTIVE.getStatus())) {
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
