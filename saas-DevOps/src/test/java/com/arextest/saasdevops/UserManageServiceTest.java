package com.arextest.saasdevops;

import com.arextest.saasdevops.service.impl.UserManageServiceImpl;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import javax.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest(classes = SaasDevOpsApplication.class)
public class UserManageServiceTest {

  private static final String COMPANY_DATABASE_FORMAT = "%s_arex_storage_db";

  @Resource
  private UserManageServiceImpl userManageService;

  @Autowired
  private MongoClient mongoClient;

  @Test
  public void addTenantToken() {
    String tenantCode = "ctrip";
    String tenantToken = "ctrip:S13V3lnts9q9gnLwDp8w7M32CQtlwkVyUaHs2P+G9lAst4LRtssybxxWFaKoAkAuF1uuS03LaGv40wlgRLCH/Q==";

    // create company database and user
    String databaseName = String.format(COMPANY_DATABASE_FORMAT, tenantCode);
    MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
    userManageService.insertTenantTokenToSystemConfiguration(tenantToken, System.currentTimeMillis(),
        mongoDatabase);
  }

}
