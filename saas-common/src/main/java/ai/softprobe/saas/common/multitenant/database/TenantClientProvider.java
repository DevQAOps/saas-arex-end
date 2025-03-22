package ai.softprobe.saas.common.multitenant.database;

public interface TenantClientProvider {

  TenantMongoClientHolder loadDefault();

  TenantMongoClientHolder load(String tenant);
}
