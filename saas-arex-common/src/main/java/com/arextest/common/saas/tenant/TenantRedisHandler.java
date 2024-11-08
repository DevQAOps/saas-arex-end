package com.arextest.common.saas.tenant;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.saas.model.SaasSystemConfigurationKeySummary;
import com.arextest.common.saas.model.dao.SaasSystemConfigurationCollection.SubscribeInfo;
import com.arextest.common.saas.model.dto.SaasSystemConfiguration;
import com.arextest.common.saas.repository.SaasSystemConfigurationRepository;
import com.arextest.common.saas.utils.RedisKeyBuilder;
import com.arextest.common.utils.TenantContextUtil;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class TenantRedisHandler {

  private static final long TENANT_STATUS_EXPIRE_SECONDS = 2 * 60 * 60;

  private final CacheProvider cacheProvider;

  private final ObjectMapper objectMapper;

  private final SaasSystemConfigurationRepository saasSystemConfigurationRepository;

  public boolean saveTenantStatusExpire(String tenantCode,
      TenantStatusRedisInfo tenantStatusRedisInfo) {
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
    TenantStatusRedisInfo tenantStatus = getTenantStatusFromRedis(tenantCode);
    // if redis not found, query from saasDb by arex-saas-service
    if (tenantStatus == null) {
      tenantStatus = getTenantStatusFromDB();
      // save to redis
      saveTenantStatusExpire(tenantCode, tenantStatus);
    }
    return tenantStatus;
  }

  private TenantStatusRedisInfo getTenantStatusFromRedis(String tenantCode) {
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


}
