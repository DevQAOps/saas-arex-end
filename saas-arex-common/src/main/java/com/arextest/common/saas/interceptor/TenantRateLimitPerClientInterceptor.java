
package com.arextest.common.saas.interceptor;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.utils.TenantContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

/**
 * @author: QizhengMo
 * @date: 2024/6/21 16:16
 */
@Slf4j
@Getter
public class TenantRateLimitPerClientInterceptor extends AbstractInterceptorHandler {
  private static final String SEPARATOR = ":";
  private final CacheProvider cacheProvider;
  private final List<Config> configs;
  private final Map<String, Config> configMap;

  public TenantRateLimitPerClientInterceptor(CacheProvider cacheProvider, List<Config> configs) {
    this.cacheProvider = cacheProvider;
    this.configs = configs;
    this.configMap = configs.stream().collect(Collectors.toMap(Config::getPath, config -> config));
  }

  @Override
  public Integer getOrder() {
    return Integer.MAX_VALUE;
  }

  @Override
  public List<String> getPathPatterns() {
    return configs.stream().map(Config::getPath).collect(Collectors.toList());
  }

  @Override
  public List<String> getExcludePathPatterns() {
    return Collections.emptyList();
  }

  @Override
  public boolean preHandle(@NonNull HttpServletRequest httpServletRequest,
      @NonNull HttpServletResponse httpServletResponse, @NonNull Object o) throws Exception {
    Config config = configMap.get(httpServletRequest.getRequestURI());

    RedissonClient redisson = getCacheProvider().getRedissionClient();
    RRateLimiter limiter = redisson.getRateLimiter(buildKey(httpServletRequest));
    limiter.trySetRate(RateType.OVERALL, config.getLimitPerMinute(), 1, RateIntervalUnit.MINUTES);

    if (limiter.tryAcquire()) {
      return true;
    } else {
      // https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/429
      httpServletResponse.sendError(429, "Rate limit exceeded");
      return false;
    }
  }

  private String buildKey(HttpServletRequest request) {
    return TenantContextUtil.getTenantCode()
        + SEPARATOR
        + request.getRequestURI()
        + SEPARATOR
        + getClientIP(request);
  }

  private String getClientIP(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null){
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }

  @Data
  @Builder
  public static class Config {
    private String path;
    private Integer limitPerMinute;
  }
}
