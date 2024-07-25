package com.arextest.saasdevops.service.impl;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.saas.model.Constants;
import com.arextest.common.saas.model.SaasSystemConfigurationKeySummary;
import com.arextest.common.saas.model.TenantUsageDocument;
import com.arextest.common.saas.model.dao.SaasSystemConfigurationCollection.SubscribeInfo;
import com.arextest.common.saas.model.dto.SaasSystemConfiguration;
import com.arextest.common.saas.repository.SaasSystemConfigurationRepository;
import com.arextest.common.saas.repository.impl.UsageStatDao;
import com.arextest.common.utils.TenantContextUtil;
import com.arextest.saasdevops.model.contract.QueryTenantUsageRequest;
import com.arextest.saasdevops.model.contract.UpdateSubScribeRequest;
import com.arextest.saasdevops.service.UsageService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author wildeslam.
 * @create 2024/6/17 16:32
 */
@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

  private final UsageStatDao usageStatDao;
  private final SaasSystemConfigurationRepository saasSystemConfigurationRepository;
  private final CacheProvider cacheProvider;
  private static final Long TWO_DAYS_MILLIS = 2 * 24 * 60 * 60 * 1000L;
  private static final Long ONE_MONTH_MILLIS = 30 * 24 * 60 * 60 * 1000L;

  @Override
  public Long queryUsage(QueryTenantUsageRequest request) {
    List<TenantUsageDocument> tenantUsageDocuments = usageStatDao.query(
        request.getTenantCode(), request.getIn(), request.getStartTime(), request.getEndTime());
    Long sum = 0L;
    for (TenantUsageDocument tenantUsageDocument : tenantUsageDocuments) {
      sum += tenantUsageDocument.getContentLengthSum();
    }
    return sum;
  }

  @Override
  public boolean updateSubScribe(UpdateSubScribeRequest request) {
    if (request.getTrafficLimit() == null && request.getStart() == null
        && request.getEnd() == null) {
      return false;
    }
    SaasSystemConfiguration subscribeConfig = new SaasSystemConfiguration();
    subscribeConfig.setKey(SaasSystemConfigurationKeySummary.SAAS_SUBSCRIBE_INFO);
    subscribeConfig.setSubscribeInfo(
        new SubscribeInfo(request.getTrafficLimit(), request.getStart(), request.getEnd()));
    return saasSystemConfigurationRepository.save(subscribeConfig);
  }

  @Scheduled(cron = "0 0 23 * * ?")
  public void statistics() {
    List<String> recentTenantCodes = usageStatDao.queryTenantCodes(System.currentTimeMillis() - ONE_MONTH_MILLIS);
    recentTenantCodes.forEach(tenant -> {
      Long totalLength = getTotalLength(tenant);
      // T+1
      LocalDate date = LocalDate.now().plusDays(1);
      String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      byte[] key = String.format(Constants.TENANT_TRAFFIC_LIMIT_KEY, formattedDate, tenant)
          .getBytes(StandardCharsets.UTF_8);
      byte[] value = totalLength.toString().getBytes(StandardCharsets.UTF_8);
      cacheProvider.put(key, TWO_DAYS_MILLIS, value);
    });
  }

  private Long getTotalLength(String tenant) {
    List<String> keys = Collections.singletonList(
        SaasSystemConfigurationKeySummary.SAAS_SUBSCRIBE_INFO);

    TenantContextUtil.setTenantCode(tenant);
    List<SaasSystemConfiguration> systemConfigurations = saasSystemConfigurationRepository.query(
        keys);
    TenantContextUtil.clear();

    SubscribeInfo subscribeInfo = Optional.ofNullable(systemConfigurations)
        .map(configurations -> CollectionUtils.isEmpty(configurations) ? null
            : configurations.get(0))
        .map(SaasSystemConfiguration::getSubscribeInfo)
        .orElse(null);
    Long start = Optional.ofNullable(subscribeInfo)
        .map(SubscribeInfo::getStart)
        .orElse(0L);
    Long end = Optional.ofNullable(subscribeInfo)
        .map(SubscribeInfo::getEnd)
        .orElse(System.currentTimeMillis());
    return usageStatDao.statistics(tenant, start, end);
  }
}
