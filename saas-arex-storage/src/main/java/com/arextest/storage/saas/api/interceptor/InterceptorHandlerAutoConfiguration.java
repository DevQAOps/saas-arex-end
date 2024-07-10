package com.arextest.storage.saas.api.interceptor;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.jwt.JWTService;
import com.arextest.common.saas.interceptor.SaasAuthorizationInterceptor;
import com.arextest.common.saas.interceptor.TenantInterceptor;
import com.arextest.common.saas.interceptor.TenantLimitService;
import com.arextest.common.saas.interceptor.TenantTrafficLimitInterceptor;
import com.arextest.common.saas.repository.SaasSystemConfigurationRepository;
import com.arextest.common.saas.repository.impl.UsageStatDao;
import com.arextest.common.saas.tenant.TenantRedisHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * the order of the interceptor AgentAccessInterceptorHandler-> TenantInterceptor ->
 * SaasAuthorizationInterceptor
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

  @Bean
  public AbstractInterceptorHandler agentAccessInterceptorHandler(
      @Value("${arex.agent.aesKey:}") String aesKey, ObjectMapper objectMapper,
      TenantRedisHandler tenantRedisHandler) {
    return new AgentAccessInterceptorHandler(aesKey, objectMapper, tenantRedisHandler);
  }

  private List<String> getTenantPathPatterns() {
    return Collections.singletonList("/**");
  }

  private List<String> getTenantExcludePathPatterns() {
    return Lists.newArrayList("/error", "/favicon.ico");
  }

  private List<String> getAuthorizationPathPatterns() {
    return Collections.singletonList("/**");
  }

  private List<String> getAuthorizationExcludePathPatterns() {
    return Lists.newArrayList("/error", "/favicon.ico", "/vi/health", "/api/config/agent/**",
        "/api/storage/record/**");
  }

  @Bean
  public AbstractInterceptorHandler trafficLimitInterceptor(
      UsageStatDao usageStatDao,
      CacheProvider cacheProvider,
      SaasSystemConfigurationRepository saasSystemConfigurationRepository) {
    return new TenantTrafficLimitInterceptor(usageStatDao, cacheProvider,
        saasSystemConfigurationRepository);
  }
}
