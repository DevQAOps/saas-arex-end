package com.arextest.common.saas.multitenant.usage;

import com.arextest.common.saas.dao.UsageStatDao;
import com.arextest.common.saas.model.TenantUsageDocument;
import com.arextest.common.saas.model.TenantUsageDocument.Meta;
import com.arextest.common.utils.TenantContextUtil;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    usageCache.compute(key, (k, v) -> v == null ? in : v + out);
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
