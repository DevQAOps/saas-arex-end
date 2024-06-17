package com.arextest.saasdevops.service.impl;

import com.arextest.common.saas.model.TenantUsageDocument;
import com.arextest.common.saas.repository.impl.UsageStatDao;
import com.arextest.saasdevops.service.UsageService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wildeslam.
 * @create 2024/6/17 16:32
 */
@Service
public class UsageServiceImpl implements UsageService {
  private static final String DATABASE_NAME = "arex_storage_db";
  private static final String CONTENT_LENGTH_SUM_FIELD = "contentLengthSum";

  @Autowired
  private MongoClient mongoClient;

  @Override
  public Long queryUsage(String tenantCode, Boolean in) {
    MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);
    MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("TenantUsage");
    Document query = new Document("meta.tenantCode", tenantCode);
    if (in != null) {
      query.append("meta.in", in);
    }
    List<Document> tenantUsageDocumentList = mongoCollection.find(query).into(new ArrayList<>());
    Long sum = 0L;
    for (Document tenantUsageDocument : tenantUsageDocumentList) {
      sum += tenantUsageDocument.getLong(CONTENT_LENGTH_SUM_FIELD);
    }
    return sum;
  }
}
