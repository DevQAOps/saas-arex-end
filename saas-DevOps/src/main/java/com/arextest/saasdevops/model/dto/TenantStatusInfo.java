package com.arextest.saasdevops.model.dto;

import com.arextest.common.saas.enums.TenantStatus;
import lombok.Data;

@Data
public class TenantStatusInfo {

  private String tenantCode;

  private String tenantToken;

  /**
   * @see TenantStatus
   */
  private Integer tenantStatus;

}
