package com.arextest.schedule.saas.api.bean;

import com.arextest.common.saas.multitenant.database.DefaultTenantClientProvider;
import com.arextest.common.saas.multitenant.database.MultiTenantMongoDbFactory;
import com.arextest.common.saas.multitenant.database.TenantClientProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;

/**
 * @author: QizhengMo
 * @date: 2024/4/2 15:58
 */
@Configuration
public class SaasDatabaseConfiguration {

  @Bean
  public TenantClientProvider tenantClientProvider() {
    return new DefaultTenantClientProvider();
  }

  @Bean
  public MongoDatabaseFactory mongoDatabaseFactory(TenantClientProvider tenantClientProvider) {
    return new MultiTenantMongoDbFactory(tenantClientProvider);
  }
}
