package com.arextest.common.saas.interceptor;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.saas.model.SaasSystemConfigurationKeySummary;
import com.arextest.common.saas.model.dto.SaasSystemConfiguration;
import com.arextest.common.saas.repository.SaasSystemConfigurationRepository;
import com.arextest.common.saas.repository.impl.UsageStatDao;
import com.arextest.common.saas.utils.TenantUtil;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wildeslam.
 * @create 2024/7/9 14:44
 */
@Slf4j
@Getter
public class TenantTrafficLimitInterceptor extends AbstractInterceptorHandler {

  private final UsageStatDao usageStatDao;

  private final CacheProvider cacheProvider;

  private final SaasSystemConfigurationRepository saasSystemConfigurationRepository;

  private static final String TENANT_TRAFFIC_LIMIT_KEY = "tenant_traffic_limit_%s";

  public TenantTrafficLimitInterceptor(
      UsageStatDao usageStatDao,
      CacheProvider cacheProvider,
      SaasSystemConfigurationRepository saasSystemConfigurationRepository) {
    this.usageStatDao = usageStatDao;
    this.cacheProvider = cacheProvider;
    this.saasSystemConfigurationRepository = saasSystemConfigurationRepository;
  }

  @Override
  public Integer getOrder() {
    return 3;
  }

  @Override
  public List<String> getPathPatterns() {
    return Collections.singletonList("/**");
  }

  @Override
  public List<String> getExcludePathPatterns() {
    return Arrays.asList("/error", "/favicon.ico", "/vi/health");
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String tenantCode = TenantUtil.extractTenantCode(request);
    if (tenantCode == null) {
      LOGGER.error("tenantCode is empty, reject the request, path:{}", request.getRequestURI());
      return false;
    }
    byte[] key = String.format(TENANT_TRAFFIC_LIMIT_KEY, tenantCode)
        .getBytes(StandardCharsets.UTF_8);
    byte[] value = cacheProvider.get(key);
    long trafficStats = value == null ? 0L : Long.parseLong(new String(value));

    List<String> keys = Collections.singletonList(
        SaasSystemConfigurationKeySummary.SAAS_TRAFFIC_LIMIT);
    List<SaasSystemConfiguration> systemConfigurations = saasSystemConfigurationRepository.query(keys);
    Long trafficLimit = Optional.ofNullable(systemConfigurations)
        .map(configurations -> configurations.get(0))
        .map(SaasSystemConfiguration::getTrafficLimit)
        .orElse(0L);

    return trafficLimit >= trafficStats;
  }
}
