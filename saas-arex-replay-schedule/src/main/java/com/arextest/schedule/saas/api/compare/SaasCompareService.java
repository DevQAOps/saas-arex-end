package com.arextest.schedule.saas.api.compare;

import com.arextest.common.utils.GroupContextUtil;
import com.arextest.diff.model.CompareOptions;
import com.arextest.diff.model.CompareResult;
import com.arextest.diff.sdk.CompareSDK;
import com.arextest.schedule.comparer.CompareConfigService;
import com.arextest.schedule.comparer.CompareService;
import com.arextest.web.model.contract.contracts.config.SystemConfigWithProperties;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Slf4j
public class SaasCompareService implements CompareService {

  private CompareConfigService compareConfigService;

  public SaasCompareService() {
  }

  public SaasCompareService(CompareConfigService compareConfigService) {
    this.compareConfigService = compareConfigService;
  }

  private LoadingCache<String, CompareSDK> CompareSDKCache =
      Caffeine.newBuilder()
          .maximumSize(100)
          .removalListener(((key, value, cause) -> {
            LOGGER.info("CompareSDK expire, key : {}, cause : {}", key, cause);
          }))
          .expireAfterWrite(2, TimeUnit.HOURS)
          .build(new CompareSDKCacheLoader());


  @Override
  public CompareResult compare(String baseMsg, String testMsg) {
    return executeComparison(baseMsg, testMsg, null,
        (compareSDK, base, test, options) -> compareSDK.compare(base, test));
  }

  @Override
  public CompareResult compare(String baseMsg, String testMsg, CompareOptions compareOptions) {
    return executeComparison(baseMsg, testMsg, compareOptions,
        (compareSDK, base, test, options) -> compareSDK.compare(base, test, options));
  }

  @Override
  public CompareResult quickCompare(String baseMsg, String testMsg) {
    return executeComparison(baseMsg, testMsg, null,
        (compareSDK, base, test, options) -> compareSDK.quickCompare(base, test));
  }

  @Override
  public CompareResult quickCompare(String baseMsg, String testMsg, CompareOptions compareOptions) {
    return executeComparison(baseMsg, testMsg, compareOptions,
        (compareSDK, base, test, options) -> compareSDK.quickCompare(base, test, options));
  }

  private CompareResult executeComparison(String baseMsg, String testMsg,
      CompareOptions compareOptions, ComparisonExecutor executor) {
    String group = GroupContextUtil.getGroup();
    CompareSDK compareSDK = CompareSDKCache.get(group);
    if (compareSDK == null) {
      LOGGER.error("SaasCompareSDK not found, group : {}, action : {}", group, "compare");
      return buildNotFoundException(baseMsg, testMsg);
    }
    return executor.execute(compareSDK, baseMsg, testMsg, compareOptions);
  }

  @FunctionalInterface
  private interface ComparisonExecutor {

    CompareResult execute(CompareSDK compareSDK, String baseMsg, String testMsg,
        CompareOptions compareOptions);
  }

  private class CompareSDKCacheLoader implements CacheLoader<String, CompareSDK> {

    @Nullable
    @Override
    public CompareSDK load(@NonNull String key) {
      CompareSDK compareSDK = new CompareSDK();
      SystemConfigWithProperties comparisonSystemConfig = compareConfigService.getComparisonSystemConfig();
      compareSDK.getGlobalOptions()
          .putPluginJarUrl(comparisonSystemConfig.getComparePluginInfo() == null ? null
              : comparisonSystemConfig.getComparePluginInfo().getComparePluginUrl())
          .putNameToLower(comparisonSystemConfig.getCompareNameToLower())
          .putNullEqualsEmpty(comparisonSystemConfig.getCompareNullEqualsEmpty())
          .putIgnoredTimePrecision(comparisonSystemConfig.getCompareIgnoreTimePrecisionMillis())
          .putIgnoreNodeSet(comparisonSystemConfig.getIgnoreNodeSet())
          .putSelectIgnoreCompare(comparisonSystemConfig.getSelectIgnoreCompare())
          .putOnlyCompareCoincidentColumn(comparisonSystemConfig.getOnlyCompareCoincidentColumn())
          .putUuidIgnore(comparisonSystemConfig.getUuidIgnore())
          .putIpIgnore(comparisonSystemConfig.getIpIgnore());
      return compareSDK;
    }
  }

  private CompareResult buildNotFoundException(String baseMsg, String testMsg) {
    return CompareSDK.fromException(baseMsg, testMsg, "CompareSDK not found");
  }

}
