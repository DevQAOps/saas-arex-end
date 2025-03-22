package ai.softprobe.saas.schedule.api.interceptor;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.jwt.JWTService;
import ai.softprobe.saas.common.interceptor.SaasAuthorizationInterceptor;
import ai.softprobe.saas.common.interceptor.TenantInterceptor;
import ai.softprobe.saas.common.interceptor.TenantLimitService;
import ai.softprobe.saas.common.repository.SaasSystemConfigurationRepository;
import ai.softprobe.saas.common.tenant.TenantRedisHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
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
      ObjectMapper objectMapper,
      SaasSystemConfigurationRepository saasSystemConfigurationRepository) {
    return new TenantRedisHandler(cacheProvider, objectMapper, saasSystemConfigurationRepository);
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
