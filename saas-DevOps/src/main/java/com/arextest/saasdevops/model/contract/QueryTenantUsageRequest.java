package com.arextest.saasdevops.model.contract;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wildeslam.
 * @create 2024/6/17 15:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryTenantUsageRequest extends BaseRequest {
  private Boolean in;
  private Long startTime;
  private Long endTime;
}