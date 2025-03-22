package ai.softprobe.saas.devops.config;

import com.arextest.common.cache.CacheProvider;
import ai.softprobe.saas.common.repository.SaasSystemConfigurationRepository;
import ai.softprobe.saas.common.repository.impl.SaasSystemConfigurationRepositoryImpl;
import ai.softprobe.saas.common.tenant.TenantRedisHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class DevopsServiceAutoConfiguration {

  @Bean
  public TenantRedisHandler tenantRedisHandler(CacheProvider cacheProvider,
      ObjectMapper objectMapper, SaasSystemConfigurationRepository saasSystemConfigurationRepository) {
    return new TenantRedisHandler(cacheProvider, objectMapper, saasSystemConfigurationRepository);
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
