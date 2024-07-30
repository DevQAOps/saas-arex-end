package com.arextest.saasdevops.service.impl;

import com.arextest.common.saas.model.SaasSystemConfigurationKeySummary;
import com.arextest.common.saas.model.dao.SaasSystemConfigurationCollection;
import com.arextest.common.saas.tenant.TenantRedisHandler;
import com.arextest.config.model.dao.config.SystemConfigurationCollection;
import com.arextest.model.mock.AREXMocker;
import com.arextest.model.mock.MockCategoryType;
import com.arextest.saasdevops.mapper.TenantStatusMapper;
import com.arextest.saasdevops.model.contract.FinalizeSaasUserRequest;
import com.arextest.saasdevops.model.contract.InitSaasUserRequest;
import com.arextest.saasdevops.model.contract.UserType;
import com.arextest.saasdevops.model.dto.TenantStatusInfo;
import com.arextest.saasdevops.repository.UserRepository;
import com.arextest.saasdevops.service.TenantManageService;
import com.arextest.saasdevops.service.UserManageService;
import com.arextest.storage.enums.MongoCollectionIndexConfigEnum;
import com.arextest.storage.enums.MongoCollectionIndexConfigEnum.FieldConfig;
import com.arextest.storage.enums.MongoCollectionIndexConfigEnum.TtlIndexConfig;
import com.arextest.storage.repository.ProviderNames;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wildeslam.
 * @create 2024/4/17 14:26
 */
@Slf4j
@Service
public class UserManageServiceImpl implements UserManageService {

  private static final String COMPANY_DATABASE_FORMAT = "%s_arex_storage_db";
  private static final String MONGO_DATABASE_PASSWORD = "iLoveArex";
  private static final String COLLECTION_SUFFIX = "Mocker";
  private static final String EXPIRATION_TIME_COLUMN_NAME = "expirationTime";


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

    // create collection & index
    createCollectionAndIndex(mongoDatabase);

    // insert jwt seed to system configuration
    insertJwtSeedToSystemConfiguration(tenantCode, currentTime, mongoDatabase);

    // insert tenant to tenant collection
    insertTenantTokenToSystemConfiguration(request.getTenantToken(), currentTime, mongoDatabase);

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
  public boolean addUser(String tenantCode, List<UserType> emails) {
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


  private void createCollectionAndIndex(MongoDatabase mongoDatabase) {
    for (MongoCollectionIndexConfigEnum indexConfigEnum : MongoCollectionIndexConfigEnum.values()) {
      setIndexByEnum(indexConfigEnum, mongoDatabase);
    }
    ensureMockerQueryIndex(mongoDatabase);
  }

  private void setIndexByEnum(MongoCollectionIndexConfigEnum indexConfigEnum,
      MongoDatabase mongoDatabase) {
    MongoCollection<Document> collection = mongoDatabase.getCollection(
        indexConfigEnum.getCollectionName());

    ListIndexesIterable<Document> existedIndexes = collection.listIndexes();

    List<Pair<Document, IndexOptions>> toAddIndexes = new ArrayList<>();

    indexConfigEnum.getIndexConfigs().forEach(indexConfig -> {
      List<FieldConfig> fieldConfigs = indexConfig.getFieldConfigs();
      Document index = new Document();
      for (FieldConfig fieldConfig : fieldConfigs) {
        index.append(fieldConfig.getFieldName(),
            fieldConfig.getAscending() != Boolean.FALSE ? 1 : -1);
      }
      IndexOptions indexOptions = new IndexOptions();
      if (indexConfig.getUnique() != null) {
        indexOptions.unique(indexConfig.getUnique());
      }
      if (indexConfig.getTtlIndexConfig() != null) {
        TtlIndexConfig ttlIndexConfig = indexConfig.getTtlIndexConfig();
        indexOptions.expireAfter(ttlIndexConfig.getExpireAfter(), ttlIndexConfig.getTimeUnit());
      }
      indexOptions.background(true);
      toAddIndexes.add(Pair.of(index, indexOptions));
    });

    // add new indexes which not exist
    for (Pair<Document, IndexOptions> newIndex : toAddIndexes) {
      try {
        collection.createIndex(newIndex.getLeft(), newIndex.getRight());
      } catch (Exception e) {
        LOGGER.error("Failed to create index: {}", newIndex.getLeft(), e);
      }
    }
  }

  private void ensureMockerQueryIndex(MongoDatabase database) {
    for (MockCategoryType category : MockCategoryType.DEFAULTS) {
      for (Field field : ProviderNames.class.getDeclaredFields()) {
        String providerName = null;
        try {
          providerName = (String) field.get(ProviderNames.class);
        } catch (IllegalAccessException e) {
          LOGGER.error("get provider name failed", e);
          continue;
        }

        MongoCollection<AREXMocker> collection =
            database.getCollection(getCollectionName(category, providerName),
                AREXMocker.class);
        try {
          Document index = new Document();
          index.append(AREXMocker.Fields.recordId, 1);
          collection.createIndex(index);
        } catch (MongoCommandException e) {
          LOGGER.info("create index failed for {}", category.getName(), e);
        }

        try {
          Document index = new Document();
          index.append(AREXMocker.Fields.appId, 1);
          index.append(AREXMocker.Fields.operationName, 1);
          collection.createIndex(index);
        } catch (MongoCommandException e) {
          LOGGER.info("create index failed for {}", category.getName(), e);
        }

        if (providerName.equals(ProviderNames.DEFAULT)) {
          setTTLIndexInMockerCollection(category, database);
        }
      }
    }
  }

  private String getCollectionName(MockCategoryType category, String providerName) {
    return providerName + category.getName() + COLLECTION_SUFFIX;
  }

  private void setTTLIndexInMockerCollection(MockCategoryType category,
      MongoDatabase mongoDatabase) {
    String categoryName = getCollectionName(category, ProviderNames.DEFAULT);
    MongoCollection<AREXMocker> collection = mongoDatabase.getCollection(categoryName,
        AREXMocker.class);
    Bson index = new Document(EXPIRATION_TIME_COLUMN_NAME, 1);
    IndexOptions indexOptions = new IndexOptions().expireAfter(0L, TimeUnit.SECONDS);
    indexOptions.background(true);
    try {
      collection.createIndex(index, indexOptions);
    } catch (MongoCommandException e) {
      // ignore
      collection.dropIndex(index);
      collection.createIndex(index, indexOptions);
    }
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

  public void insertTenantTokenToSystemConfiguration(String tenantToken, Long currentTime,
      MongoDatabase mongoDatabase) {
    MongoCollection<SaasSystemConfigurationCollection> saasSystemConfigurationCollection =
        mongoDatabase.getCollection("SystemConfiguration", SaasSystemConfigurationCollection.class);
    SaasSystemConfigurationCollection systemConfiguration = new SaasSystemConfigurationCollection();
    systemConfiguration.setTenantToken(tenantToken);
    systemConfiguration.setKey(SaasSystemConfigurationKeySummary.SAAS_TENANT_TOKEN);
    systemConfiguration.setDataChangeCreateTime(currentTime);
    systemConfiguration.setDataChangeUpdateTime(currentTime);
    saasSystemConfigurationCollection.insertOne(systemConfiguration);
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
