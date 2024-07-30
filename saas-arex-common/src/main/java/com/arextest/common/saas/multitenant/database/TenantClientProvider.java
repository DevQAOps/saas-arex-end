package com.arextest.common.saas.multitenant.database;

public interface TenantClientProvider {

  TenantMongoClientHolder loadDefault();

  TenantMongoClientHolder load(String tenant);
}
