package com.arextest.saasdevops.config;

import com.arextest.common.saas.multitenant.database.DefaultTenantClientProvider;
import com.arextest.common.saas.multitenant.database.MultiTenantMongoDbFactory;
import com.arextest.common.saas.multitenant.database.TenantClientProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;

/**
 * @author wildeslam.
 * @create 2024/4/16 20:33
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

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory, MongoConverter converter) {
        return new MongoTemplate(mongoDatabaseFactory, converter);
    }
}
