package com.arextest.common.saas.repository.impl;

import com.arextest.common.saas.model.TenantUsageDocument;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * @author: QizhengMo
 * @date: 2024/5/21 11:10
 */
@Repository
@RequiredArgsConstructor
public class UsageStatDao {
  private final MongoTemplate mongoTemplate;
  private static final String TOTAL_LENGTH = "total_length";
  private static final String TIMESTAMP = "timestamp";
  private static final String TENANT_CODE = "meta.tenantCode";
  private static final String IN = "meta.in";
  private static final String COLLECTION_NAME = "TenantUsage";

  @EventListener(ApplicationReadyEvent.class)
  public void ensureCollection() {
    // spring data mongo does not create TimeSeries collection automatically,
    // this need to be done before any data is inserted
    try {
      mongoTemplate.createCollection(TenantUsageDocument.class);
    } catch (Exception e) {
      // ignore if collection already exists
    }
  }

  public void save(TenantUsageDocument doc) {
    mongoTemplate.save(doc);
  }

  public List<TenantUsageDocument> query(String tenantCode, Boolean in, Long from, Long to) {
    Criteria criteria = Criteria.where(TENANT_CODE).is(tenantCode);
    if (in != null) {
      criteria.and(IN).is(in);
    }
    from = from == null ? 0 : from;
    to = to == null ? System.currentTimeMillis() : to;
    criteria.and(TIMESTAMP).gte(new Date(from)).lt(new Date(to));
    Query query = new Query(criteria);
    return mongoTemplate.find(query, TenantUsageDocument.class);
  }

  // 查出所有满足条件的数据，对contentLengthSum求和
  public Long statistics(String tenantCode, Long from, Long to) {
    Criteria criteria = Criteria.where(TENANT_CODE).is(tenantCode);
    from = from == null ? 0 : from;
    to = to == null ? System.currentTimeMillis() : to;
    criteria.and(TIMESTAMP).gte(new Date(from)).lt(new Date(to));

    GroupOperation groupOperation = Aggregation.group().sum("contentLengthSum").as(TOTAL_LENGTH);
    Aggregation aggregation = Aggregation.newAggregation(
        Aggregation.match(criteria),
        groupOperation
    );
    AggregationResults<Document> result = mongoTemplate.aggregate(
        aggregation, COLLECTION_NAME , Document.class
    );

    List<Document> documents = result.getMappedResults();
    if (documents.isEmpty()) {
      return 0L;
    }

    Document document = documents.get(0);
    return document.getLong(TOTAL_LENGTH);
  }

  public List<String> queryTenantCodes(Long from) {
    if (from == null) {
      from = 0L;
    }
    // 从from时间点开始，按tenantCode分组，取出所有tenantCode
    GroupOperation groupOperation = Aggregation.group(TENANT_CODE);
    Aggregation aggregation = Aggregation.newAggregation(
        Aggregation.match(Criteria.where(TIMESTAMP).gte(new Date(from))),
        groupOperation
    );
    AggregationResults<Document> result = mongoTemplate.aggregate(
        aggregation, COLLECTION_NAME, Document.class
    );

    List<Document> documents = result.getMappedResults();
    return documents.stream().map(doc -> doc.getString("_id")).collect(Collectors.toList());
  }
}
