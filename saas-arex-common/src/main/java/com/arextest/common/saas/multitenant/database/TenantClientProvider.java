package com.arextest.common.saas.multitenant.database;

import java.util.List;

public interface TenantClientProvider {
  TenantMongoClientHolder loadDefault();
  TenantMongoClientHolder load(String tenant);
}
