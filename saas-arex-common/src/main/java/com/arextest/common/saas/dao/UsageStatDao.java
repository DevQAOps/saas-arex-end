package com.arextest.common.saas.dao;

import com.arextest.common.saas.model.TenantUsageDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
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
}
