package com.arextest.common.saas.model;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TimeSeries;
import org.springframework.data.mongodb.core.timeseries.Granularity;

/**
 * @author: QizhengMo
 * @date: 2024/5/20 20:24
 */
@TimeSeries(collection = "TenantUsage", timeField = "timestamp", granularity = Granularity.SECONDS, metaField = "meta")
@Getter
@Setter
public class TenantUsageDocument {
  private Timestamp timestamp;
  private Long contentLengthSum;
  private Meta meta;

  @Getter
  @Setter
  public static class Meta {
    private String tenantCode;
    private boolean in;
    private String endpoint;
  }
}
