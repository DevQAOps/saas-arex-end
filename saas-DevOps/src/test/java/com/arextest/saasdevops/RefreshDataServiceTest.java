package com.arextest.saasdevops;

import com.arextest.common.saas.model.SaasSystemConfigurationKeySummary;
import com.arextest.common.saas.model.dao.SaasSystemConfigurationCollection.SubscribeInfo;
import com.arextest.common.saas.model.dto.SaasSystemConfiguration;
import com.arextest.common.saas.repository.impl.SaasSystemConfigurationRepositoryImpl;
import com.arextest.common.utils.TenantContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoIterable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Slf4j
@Disabled
@SpringBootTest(classes = SaasDevOpsApplication.class)
public class RefreshDataServiceTest {

  @Resource(name = "saasMongoTemplate")
  private MongoTemplate saasMongoTemplate;

  @Value("${spring.data.mongodb.uri}")
  private String mongoUri;

  @Resource
  private SaasSystemConfigurationRepositoryImpl saasSystemConfigurationRepository;

  @Resource
  private ObjectMapper objectMapper;

  @Test
  public void refreshData() {

    List<String> actualName = new ArrayList<>();
    List<String> dbNames = listAllDatabases();
    for (String dbName : dbNames) {
      if (dbName.endsWith("_arex_storage_db")) {
        String dbNewName = dbName.replace("_arex_storage_db", "");
        actualName.add(dbNewName);
      }
    }

    for (String tenantCode : actualName) {

      Query query = new Query();
      query.addCriteria(Criteria.where("tenantCode").is(tenantCode));
      TenantInfo tenant = saasMongoTemplate.findOne(query, TenantInfo.class, "Tenant");

      if (tenant == null) {
        LOGGER.info("Tenant not found: {}", tenantCode);
        continue;
      }

      TenantContextUtil.setTenantCode(tenantCode);

      List<SaasSystemConfiguration> configurations = saasSystemConfigurationRepository.query(
          Collections.singleton(
              SaasSystemConfigurationKeySummary.SAAS_SUBSCRIBE_INFO)
      );

//      // 创建json文件，将configurations写入文件
//      JsonFileWriter jsonFileWriter = new JsonFileWriter(objectMapper);
//      String filePath = String.format("./systemConfig/%s.json", tenantCode);
//      jsonFileWriter.writeConfigurationsToFile(configurations, filePath);

      long trafficLimit = tenant.getTrafficLimit();
      long start = tenant.getPackageEffectiveTime();
      long end = tenant.getExpireTime();

      if (configurations == null || configurations.isEmpty()) {
        LOGGER.info("SaasSystemConfiguration not found: {}", tenantCode);
        SaasSystemConfiguration saasSystemConfiguration = new SaasSystemConfiguration();
        saasSystemConfiguration.setKey(SaasSystemConfigurationKeySummary.SAAS_SUBSCRIBE_INFO);
        SubscribeInfo subscribeInfo = new SubscribeInfo(trafficLimit, start, end);
        saasSystemConfiguration.setSubscribeInfo(subscribeInfo);
        saasSystemConfigurationRepository.save(saasSystemConfiguration);
      } else {
        SaasSystemConfiguration saasSystemConfiguration = configurations.get(0);
        SubscribeInfo subscribeInfo = new SubscribeInfo(trafficLimit, start, end);
        saasSystemConfiguration.setSubscribeInfo(subscribeInfo);
        saasSystemConfigurationRepository.save(saasSystemConfiguration);
      }
    }
  }


  private List<String> listAllDatabases() {
    List<String> dbNames = new ArrayList<>();
    try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
      MongoIterable<String> databases = mongoClient.listDatabaseNames();
      for (String dbName : databases) {
        dbNames.add(dbName);
      }
    }
    return dbNames;
  }


  @Data
  private static class Tenant {

    private String tenantCode;
    private long trafficLimit;
    private long start;
    private long end;
  }

  @Data
  private static class TenantInfo {

    private String tenantCode;
    private long trafficLimit;
    private long packageEffectiveTime;
    private long expireTime;
  }

  public class JsonFileWriter {

    private final ObjectMapper objectMapper;

    public JsonFileWriter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    public void writeConfigurationsToFile(List<SaasSystemConfiguration> configurations,
        String filePath) {
      if (configurations == null || configurations.isEmpty()) {
        return;
      }
      writeConfigurations(configurations, filePath);
    }

    public void writeConfigurations(List<SaasSystemConfiguration> configurations, String filePath) {
      try {
        File file = new File(filePath);
        if (!file.exists()) {
          file.getParentFile().mkdirs(); // Create directories if they do not exist
          file.createNewFile(); // Create the file if it does not exist
        }
        objectMapper.writeValue(file, configurations);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


}
