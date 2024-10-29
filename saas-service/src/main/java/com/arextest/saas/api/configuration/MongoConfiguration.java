package com.arextest.saas.api.configuration;

import com.arextest.web.common.LogUtils;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;

/**
 * @author wildeslam.
 * @create 2024/3/6 10:50
 */
@Slf4j
@Configuration
public class MongoConfiguration {

  @Value("${spring.data.mongodb.uri}")
  private String mongoUri;

  @Value("${spring.data.mongodb.database}")
  private String mongoDatabase;

  @Bean
  public MongoDatabaseFactory mongoDBFactory() {
    try {
      ConnectionString connectionString = new ConnectionString(mongoUri);
      String dbName = connectionString.getDatabase();
      if (dbName == null) {
        dbName = mongoDatabase;
      }

      CodecRegistry pojoCodecRegistry =
          CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
              CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

      MongoClientSettings settings = MongoClientSettings.builder()
          .applyConnectionString(connectionString)
          .codecRegistry(pojoCodecRegistry).build();
      MongoClient mongoClient = MongoClients.create(settings);
      return new SimpleMongoClientDatabaseFactory(mongoClient, dbName);
    } catch (Exception e) {
      LogUtils.error(LOGGER, "cannot connect mongodb", e);
    }
    return null;
  }

  @Bean
  public MongoTemplate mongoTemplate(MongoDatabaseFactory factory, MongoConverter converter) {
    MappingMongoConverter mappingMongoConverter = (MappingMongoConverter) converter;
    mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

    return new MongoTemplate(factory, mappingMongoConverter);
  }
}
