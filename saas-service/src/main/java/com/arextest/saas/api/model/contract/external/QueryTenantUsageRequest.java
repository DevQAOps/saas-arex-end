package com.arextest.saas.api.model.contract.external;

import lombok.Data;

/**
 * @author b_yu
 * @since 2024/7/24
 */
@Data
public class QueryTenantUsageRequest {

  private String tenantCode;
  private Boolean in;
  private Long startTime;
  private Long endTime;
}
