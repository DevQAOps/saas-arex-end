package com.arextest.common.saas.tenant;

import com.arextest.common.saas.enums.TenantStatus;
import lombok.Data;

@Data
public class TenantStatusRedisInfo {

  private String tenantToken;
  /**
   * @see TenantStatus
   */
  private Integer tenantStatus;

}