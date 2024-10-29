package com.arextest.saasdevops.config;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.saas.repository.SaasSystemConfigurationRepository;
import com.arextest.common.saas.repository.impl.SaasSystemConfigurationRepositoryImpl;
import com.arextest.common.saas.tenant.TenantRedisHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class DevopsServiceAutoConfiguration {

  @Bean
  public TenantRedisHandler tenantRedisHandler(CacheProvider cacheProvider,
      ObjectMapper objectMapper) {
    return new TenantRedisHandler(cacheProvider, objectMapper);
  }

  /*
   * for saas system configuration repository
   */
  @Bean
  public SaasSystemConfigurationRepository saasSystemConfigurationRepository(
      MongoTemplate mongoTemplate) {
    return new SaasSystemConfigurationRepositoryImpl(mongoTemplate);
  }

}
