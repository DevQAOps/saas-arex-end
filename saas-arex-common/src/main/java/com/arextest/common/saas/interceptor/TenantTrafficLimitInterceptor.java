package com.arextest.common.saas.interceptor;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.saas.model.Constants;
import com.arextest.common.saas.model.SaasSystemConfigurationKeySummary;
import com.arextest.common.saas.model.dao.SaasSystemConfigurationCollection.SubscribeInfo;
import com.arextest.common.saas.model.dto.SaasSystemConfiguration;
import com.arextest.common.saas.repository.SaasSystemConfigurationRepository;
import com.arextest.common.saas.repository.impl.UsageStatDao;
import com.arextest.common.saas.utils.TenantUtil;
import com.arextest.common.utils.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author wildeslam.
 * @create 2024/7/9 14:44
 */
@Slf4j
@Getter
public class TenantTrafficLimitInterceptor extends AbstractInterceptorHandler {

  private final ObjectMapper mapper = new ObjectMapper();
  private final UsageStatDao usageStatDao;

  private final CacheProvider cacheProvider;

  private final SaasSystemConfigurationRepository saasSystemConfigurationRepository;

  @Value("${arex.saas.traffic.limit.enable}")
  private boolean enable;

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
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws IOException {
    if (!enable) {
      return true;
    }
    String tenantCode = TenantUtil.extractTenantCode(request);
    if (tenantCode == null) {
      LOGGER.error("tenantCode is empty, reject the request, path:{}", request.getRequestURI());
      return false;
    }
    LocalDate date = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedDate = date.format(formatter);
    byte[] key = String.format(Constants.TENANT_TRAFFIC_LIMIT_KEY, formattedDate, tenantCode)
        .getBytes(StandardCharsets.UTF_8);
    byte[] value = cacheProvider.get(key);
    long trafficStats = value == null ? 0L : Long.parseLong(new String(value));

    List<String> keys = Collections.singletonList(
        SaasSystemConfigurationKeySummary.SAAS_SUBSCRIBE_INFO);
    List<SaasSystemConfiguration> systemConfigurations = saasSystemConfigurationRepository.query(
        keys);
    SubscribeInfo subscribeInfo = Optional.ofNullable(systemConfigurations)
        .map(configurations -> CollectionUtils.isEmpty(configurations) ? null
            : configurations.get(0))
        .map(SaasSystemConfiguration::getSubscribeInfo)
        .orElse(null);
    long trafficLimit = Optional.ofNullable(subscribeInfo)
        .map(SubscribeInfo::getTrafficLimit)
        .orElse(0L);

    if (trafficLimit < trafficStats) {
      Response no_permission = ResponseUtils.errorResponse("Traffic Limited",
          ResponseCode.AUTHENTICATION_FAILED);
      response.getWriter().write(mapper.writeValueAsString(no_permission));
      LOGGER.info(
          String.format("Traffic Limited; path: %s", request.getServletPath()));
      return false;
    }
    return true;
  }
}
