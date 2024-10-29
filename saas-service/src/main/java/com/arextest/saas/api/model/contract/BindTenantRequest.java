package com.arextest.saas.api.model.contract;

import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/3/20 16:47
 */
@Data
public class BindTenantRequest {

  private String tenantName;
  private String tenantCode;
}
