package com.arextest.common.saas.repository.impl;

import com.arextest.common.saas.model.TenantUsageDocument;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    Criteria criteria = Criteria.where("meta.tenantCode").is(tenantCode);
    if (in != null) {
      criteria.and("meta.in").is(in);
    }
    from = from == null ? 0 : from;
    to = to == null ? Long.MAX_VALUE : to;
    criteria.and("meta.timestamp").gte(from).lt(to);
    Query query = new Query(criteria);
    return mongoTemplate.find(query, TenantUsageDocument.class);
  }
}
