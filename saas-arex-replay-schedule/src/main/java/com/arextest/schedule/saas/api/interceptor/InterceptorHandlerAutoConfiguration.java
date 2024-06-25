package com.arextest.schedule.saas.api.interceptor;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.jwt.JWTService;
import com.arextest.common.saas.interceptor.SaasAuthorizationInterceptor;
import com.arextest.common.saas.interceptor.TenantInterceptor;
import com.arextest.common.saas.interceptor.TenantLimitService;
import com.arextest.common.saas.tenant.TenantRedisHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * the order of the interceptor TenantInterceptor -> SaasAuthorizationInterceptor
 */
@Configuration
public class InterceptorHandlerAutoConfiguration {

  @Bean
  public TenantRedisHandler tenantRedisHandler(CacheProvider cacheProvider,
      ObjectMapper objectMapper) {
    return new TenantRedisHandler(cacheProvider, objectMapper);
  }

  @Bean
  public TenantLimitService tenantLimitService(TenantRedisHandler tenantRedisHandler) {
    return new TenantLimitService(tenantRedisHandler);
  }

  @Bean
  public AbstractInterceptorHandler tenantInterceptor(TenantLimitService tenantLimitService) {
    return new TenantInterceptor(tenantLimitService, getTenantPathPatterns(),
        getTenantExcludePathPatterns());
  }

  @Bean
  public AbstractInterceptorHandler authorizationInterceptor(JWTService jwtService) {
    return new SaasAuthorizationInterceptor(
        getAuthorizationPathPatterns(),
        getAuthorizationExcludePathPatterns(),
        jwtService
    );
  }

  private List<String> getTenantPathPatterns() {
    return Collections.singletonList("/**");
  }

  private List<String> getTenantExcludePathPatterns() {
    return Lists.newArrayList("/error", "/favicon.ico");
  }

  public List<String> getAuthorizationPathPatterns() {
    return Collections.singletonList("/**");
  }

  public List<String> getAuthorizationExcludePathPatterns() {
    return Lists.newArrayList("/error", "/favicon.ico", "/vi/health");
  }
}
