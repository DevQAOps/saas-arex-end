package com.arextest.common.saas.multitenant.database;

import com.arextest.common.utils.GroupContextUtil;
import com.mongodb.client.MongoDatabase;
import lombok.NonNull;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * @author: QizhengMo
 * @date: 2024/3/28 20:29
 */
public class MultiTenantMongoDbFactory extends SimpleMongoClientDatabaseFactory {
  private final TenantClientProvider tenantClientProvider;

  public MultiTenantMongoDbFactory(final TenantClientProvider tenantClientProvider) {
    super(tenantClientProvider.loadDefault().getMongoClient(),
        tenantClientProvider.loadDefault().getDatabaseName());
    this.tenantClientProvider = tenantClientProvider;
  }

  @Override
  public @NonNull MongoDatabase getMongoDatabase() throws DataAccessException {
    final String tenant = GroupContextUtil.getGroup();
    if (tenant != null) {
      return tenantClientProvider.load(tenant).getMongoDatabase();
    } else {
      return tenantClientProvider.loadDefault().getMongoDatabase();
    }
  }

  @Override
  public void destroy() throws Exception {
  }
}