package com.arextest.saasdevops.service.impl;

import com.arextest.common.saas.tenant.TenantRedisHandler;
import com.arextest.config.model.dao.config.SystemConfigurationCollection;
import com.arextest.saasdevops.mapper.TenantStatusMapper;
import com.arextest.saasdevops.model.contract.FinalizeSaasUserRequest;
import com.arextest.saasdevops.model.contract.InitSaasUserRequest;
import com.arextest.saasdevops.model.dto.TenantStatusInfo;
import com.arextest.saasdevops.repository.UserRepository;
import com.arextest.saasdevops.service.TenantManageService;
import com.arextest.saasdevops.service.UserManageService;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wildeslam.
 * @create 2024/4/17 14:26
 */
@Service
public class UserManageServiceImpl implements UserManageService {

  private static final String COMPANY_DATABASE_FORMAT = "%s_arex_storage_db";

  private static final String MONGO_DATABASE_PASSWORD = "iLoveArex";

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MongoClient mongoClient;

  @Autowired
  private TenantManageService tenantManageService;

  @Autowired
  private TenantRedisHandler tenantRedisHandler;

  @Override
  public boolean initSaasUser(InitSaasUserRequest request) {
    String tenantCode = request.getTenantCode();
    String email = request.getEmail();

    Long currentTime = System.currentTimeMillis();

    // create company database and user
    String databaseName = String.format(COMPANY_DATABASE_FORMAT, tenantCode);
    MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

    createDatabaseAndUser(databaseName, mongoDatabase);

    // insert jwt seed to system configuration
    insertJwtSeedToSystemConfiguration(tenantCode, currentTime, mongoDatabase);

    // insert user to user collection
    insertUserToUserCollection(email, currentTime, mongoDatabase);

    // init redis status
    initTenantRedisStatus(request);
    return true;
  }

  @Override
  public boolean finalizeSaasUser(FinalizeSaasUserRequest request) {
    String tenantCode = request.getTenantCode();

    String databaseName = String.format(COMPANY_DATABASE_FORMAT, tenantCode);
    MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
    deleteDatabaseAndUser(mongoDatabase);

    tenantRedisHandler.removeTenant(tenantCode);

    return false;
  }

  @Override
  public boolean addUser(String tenantCode, List<String> emails) {
    return userRepository.addUser(emails);
  }

  @Override
  public boolean removeUser(String tenantCode, List<String> emails) {
    return userRepository.removeUser(emails);
  }

  private void createDatabaseAndUser(String databaseName, MongoDatabase mongoDatabase) {
    BasicDBObject createUserCommand = new BasicDBObject("createUser", "arex")
        .append("pwd", MONGO_DATABASE_PASSWORD)
        .append("roles", Collections.singletonList(new BasicDBObject("role", "readWrite")
            .append("db", databaseName)));
    mongoDatabase.runCommand(createUserCommand);
  }

  private void deleteDatabaseAndUser(MongoDatabase mongoDatabase) {
    BasicDBObject dropUserCommand = new BasicDBObject("dropUser", "arex");
    mongoDatabase.runCommand(dropUserCommand);
    mongoDatabase.drop();
  }

  private void insertJwtSeedToSystemConfiguration(String tenantCode, Long currentTime,
      MongoDatabase mongoDatabase) {
    MongoCollection<SystemConfigurationCollection> systemConfigurationCollection =
        mongoDatabase.getCollection("SystemConfiguration", SystemConfigurationCollection.class);
    SystemConfigurationCollection systemConfiguration = new SystemConfigurationCollection();
    systemConfiguration.setJwtSeed(generateRandomCode(tenantCode));
    systemConfiguration.setKey(SystemConfigurationCollection.KeySummary.JWT_SEED);
    systemConfiguration.setDataChangeCreateTime(currentTime);
    systemConfiguration.setDataChangeUpdateTime(currentTime);
    systemConfigurationCollection.insertOne(systemConfiguration);
  }

  private void insertUserToUserCollection(String email, Long currentTime,
      MongoDatabase mongoDatabase) {
    MongoCollection<com.arextest.web.model.dao.mongodb.UserCollection> userCollection =
        mongoDatabase.getCollection("User",
            com.arextest.web.model.dao.mongodb.UserCollection.class);
    com.arextest.web.model.dao.mongodb.UserCollection user = new com.arextest.web.model.dao.mongodb.UserCollection();
    user.setUserName(email);
    user.setDataChangeCreateTime(currentTime);
    user.setDataChangeUpdateTime(currentTime);
    userCollection.insertOne(user);
  }

  private void initTenantRedisStatus(InitSaasUserRequest request) {
    // insert tenant status to redis
    TenantStatusInfo tenantStatusInfo = TenantStatusMapper.INSTANCE.contractToDto(request);
    boolean initTenantStatus = tenantManageService.initTenantStatus(tenantStatusInfo);
    if (!initTenantStatus) {
      throw new RuntimeException("initTenantStatus failed");
    }
  }


  private String generateRandomCode(String tenantCode) {
    return UUID.nameUUIDFromBytes((tenantCode + System.currentTimeMillis()).getBytes()).toString();
  }


}
