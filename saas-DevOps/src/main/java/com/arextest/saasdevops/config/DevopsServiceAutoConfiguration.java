package com.arextest.saasdevops.config;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.saas.tenant.TenantRedisHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DevopsServiceAutoConfiguration {

  @Bean
  public TenantRedisHandler tenantRedisHandler(CacheProvider cacheProvider,
      ObjectMapper objectMapper) {
    return new TenantRedisHandler(cacheProvider, objectMapper);
  }

}
