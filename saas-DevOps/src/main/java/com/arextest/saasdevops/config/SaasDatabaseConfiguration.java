package com.arextest.saasdevops.config;

import com.arextest.common.saas.multitenant.database.DefaultTenantClientProvider;
import com.arextest.common.saas.multitenant.database.MultiTenantMongoDbFactory;
import com.arextest.common.saas.multitenant.database.TenantClientProvider;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

/**
 * @author wildeslam.
 * @create 2024/4/16 20:33
 */
@Configuration
public class SaasDatabaseConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

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
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDatabaseFactory, converter);
        MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
        mongoMapping.setCustomConversions(customConversions());
        mongoMapping.afterPropertiesSet();
        return new MongoTemplate(mongoDatabaseFactory, converter);
    }

    @Bean(name = "saasMongoTemplate")
    public MongoTemplate saasMongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "saas_db");
    }

    @Bean
    public @NonNull MongoClient mongoClient() {
        CodecRegistry pojoCodecRegistry =
            CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientSettings settings = MongoClientSettings.builder()
            .codecRegistry(pojoCodecRegistry)
            .applyConnectionString(new ConnectionString(mongoUri))
            .build();
        return MongoClients.create(settings);
    }

    @Bean
    public CustomConversions customConversions() {
        List<Converter<?,?>> converters = new ArrayList<>();
        converters.add(new TimestampConverter());
        return new MongoCustomConversions(converters);
    }

}
