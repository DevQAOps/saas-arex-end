package com.arextest.common.saas.multitenant.database;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author: QizhengMo
 * @date: 2024/3/29 10:50
 */
public class DefaultTenantClientProvider implements TenantClientProvider {

  private static final String tenantDBUriSuffix = "_arex_storage_db";
  private static final String DEFAULT_TENANT = "arex_internal_default";
  @Value("${saas.tenant.database.mongo.default.uri}")
  private String defaultUri;
  @Value("${saas.tenant.database.mongo.default.database}")
  private String defaultDatabaseName;
  @Value("${saas.tenant.database.mongo.uri.base}")
  private String tenantDBUriBase;
  private final LoadingCache<String, TenantMongoClientHolder> cache = Caffeine.newBuilder()
      .maximumSize(100)
      .build((k) -> {
        // todo note it for test,
//        if (StringUtils.isEmpty(k)) {
//          throw new RuntimeException("Invalid tenant");
//        }
        // todo will remove it in the future
        if (StringUtils.isEmpty(k)) {
          return createHolderByUri(defaultUri, defaultDatabaseName);
        }

        if (DEFAULT_TENANT.equals(k)) {
          return createHolderByUri(defaultUri, defaultDatabaseName);
        } else {
          return createHolderByUri(getUriByTenant(k), getDBNameByTenant(k));
        }
      });

  @Override
  public TenantMongoClientHolder loadDefault() {
    return cache.get(DEFAULT_TENANT);
  }


  private TenantMongoClientHolder createHolderByUri(String uri, String dbName) {
    MongoClient client = MongoClients.create(uri);
    TenantMongoClientHolder holder = TenantMongoClientHolder.builder()
        .mongoClient(client)
        .databaseName(dbName)
        .build();
    createDatabase(holder);
    return holder;
  }

  private void createDatabase(TenantMongoClientHolder holder) {
    MongoClient client = holder.getMongoClient();
    MongoDatabase database = client.getDatabase(holder.getDatabaseName());
    holder.setMongoDatabase(database);
  }

  private String getUriByTenant(String tenant) {
    return tenantDBUriBase + getDBNameByTenant(tenant);
  }

  private String getDBNameByTenant(String tenant) {
    return tenant + tenantDBUriSuffix;
  }

  @Override
  public TenantMongoClientHolder load(String tenant) {
    return cache.get(tenant);
  }
}
