package com.arextest.common.saas.interceptor;

import com.arextest.common.saas.enums.SaasErrorCode;
import com.arextest.common.saas.model.SaasSystemConfigurationKeySummary;
import com.arextest.common.saas.model.dao.SaasSystemConfigurationCollection.SubscribeInfo;
import com.arextest.common.saas.model.dto.SaasSystemConfiguration;
import com.arextest.common.saas.repository.SaasSystemConfigurationRepository;
import com.arextest.common.saas.tenant.TenantRedisHandler;
import com.arextest.common.saas.tenant.TenantStatusRedisInfo;
import com.arextest.common.utils.TenantContextUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class TenantLimitService {

  private final TenantRedisHandler tenantRedisHandler;

  private final SaasSystemConfigurationRepository saasSystemConfigurationRepository;


  public TenantLimitResult limitTenant(TenantLimitInfo tenantLimitInfo) throws RuntimeException {

    String tenantCode = tenantLimitInfo.getTenantCode();
    TenantStatusRedisInfo tenantStatus = tenantRedisHandler.getTenantStatus(tenantCode);

    // if redis not found, query from saasDb by arex-saas-service
    if (tenantStatus == null) {
      tenantStatus = getTenantStatusFromDB();
      // save to redis
      tenantRedisHandler.saveTenantStatusExpire(tenantCode, tenantStatus);
    }

    // verify whether tenant exists
    if (tenantStatus.getExpireTime() == null || StringUtils.isEmpty(
        tenantStatus.getTenantToken())) {
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


  private TenantStatusRedisInfo getTenantStatusFromDB() {
    List<String> keys = Arrays.asList(
        SaasSystemConfigurationKeySummary.SAAS_TENANT_TOKEN,
        SaasSystemConfigurationKeySummary.SAAS_SUBSCRIBE_INFO
    );
    List<SaasSystemConfiguration> systemConfigurations = saasSystemConfigurationRepository.query(
        keys);

    TenantStatusRedisInfo tenantStatusRedisInfo = new TenantStatusRedisInfo();
    if (CollectionUtils.isEmpty(systemConfigurations)) {
      return tenantStatusRedisInfo;
    }

    systemConfigurations.forEach(config -> {
      if (Objects.equals(SaasSystemConfigurationKeySummary.SAAS_TENANT_TOKEN, config.getKey())) {
        tenantStatusRedisInfo.setTenantToken(config.getTenantToken());
      }
      if (Objects.equals(SaasSystemConfigurationKeySummary.SAAS_SUBSCRIBE_INFO, config.getKey())) {
        tenantStatusRedisInfo.setExpireTime(Optional.of(config.getSubscribeInfo()).map(
            SubscribeInfo::getEnd).orElse(null));
      }
    });

    if (StringUtils.isEmpty(tenantStatusRedisInfo.getTenantToken())
        || tenantStatusRedisInfo.getExpireTime() == null) {
      LOGGER.error("tenantStatus info missing, tenantCode:{}",
          TenantContextUtil.getTenantCode());
    }
    return tenantStatusRedisInfo;
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