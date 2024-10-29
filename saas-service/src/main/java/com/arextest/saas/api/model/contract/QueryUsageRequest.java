package com.arextest.saas.api.model.contract;

import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/6/18 16:50
 */
@Data
public class QueryUsageRequest {
  private Long startTime;
  private Long endTime;
}
