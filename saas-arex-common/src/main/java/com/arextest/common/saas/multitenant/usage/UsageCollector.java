package com.arextest.common.saas.multitenant.usage;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.saas.model.Constants;
import com.arextest.common.saas.model.SaasSystemConfigurationKeySummary;
import com.arextest.common.saas.model.TenantUsageDocument;
import com.arextest.common.saas.model.TenantUsageDocument.Meta;
import com.arextest.common.saas.model.dao.SaasSystemConfigurationCollection.SubscribeInfo;
import com.arextest.common.saas.model.dto.SaasSystemConfiguration;
import com.arextest.common.saas.repository.SaasSystemConfigurationRepository;
import com.arextest.common.saas.repository.impl.UsageStatDao;
import com.arextest.common.utils.TenantContextUtil;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author: QizhengMo
 * @date: 2024/5/20 19:59
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UsageCollector {

  private static volatile Map<UsageCacheKey, Long> usageCache = new ConcurrentHashMap<>(8);
  private final UsageStatDao usageStatDao;
  private final SaasSystemConfigurationRepository saasSystemConfigurationRepository;
  private final CacheProvider cacheProvider;
  private static final Long TWO_DAYS_MILLIS = 2 * 24 * 60 * 60 * 1000L;

  public static void collect(String endpoint, Long in, Long out) {
    String tenant = TenantContextUtil.getTenantCode();
    UsageCacheKey key = UsageCacheKey
        .builder()
        .tenant(tenant)
        .endpoint(endpoint)
        .in(true)
        .build();
    usageCache.compute(key, (k, v) -> v == null ? in : v + in);
    key = UsageCacheKey
        .builder()
        .tenant(tenant)
        .endpoint(endpoint)
        .in(false)
        .build();
    usageCache.compute(key, (k, v) -> v == null ? out : v + out);
  }

  @Scheduled(fixedRate = 10 * 1000L)
  public void report() {
    Map<UsageCacheKey, Long> old;
    synchronized (UsageCollector.class) {
      old = usageCache;
      usageCache = new ConcurrentHashMap<>(old.size());
    }

    try {
      old.entrySet().forEach(entry -> {
        TenantUsageDocument item = buildStatItem(entry);
        usageStatDao.save(item);
      });
    } catch (Exception e) {
      LOGGER.error("Failed to save usage stat", e);
    }
  }

  // every day at 23:00
  @Scheduled(cron = "0 0 23 * * ?")
  public void statistics() {
    List<String> test = Arrays.asList("trip", "wilde123");
    usageCache.keySet().stream()
        .map(UsageCacheKey::getTenant)
        .distinct()
        .forEach(tenant -> {
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
    TenantContextUtil.clearAll();

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

  private static TenantUsageDocument buildStatItem(Entry<UsageCacheKey, Long> entry) {
    UsageCacheKey k = entry.getKey();
    Long v = entry.getValue();
    TenantUsageDocument statItem = new TenantUsageDocument();
    Meta statMeta = new Meta();
    statMeta.setTenantCode(k.getTenant());
    statMeta.setIn(k.isIn());
    statMeta.setEndpoint(k.getEndpoint());

    statItem.setMeta(statMeta);
    statItem.setContentLengthSum(v);
    statItem.setTimestamp(new Timestamp(System.currentTimeMillis()));
    return statItem;
  }
}
