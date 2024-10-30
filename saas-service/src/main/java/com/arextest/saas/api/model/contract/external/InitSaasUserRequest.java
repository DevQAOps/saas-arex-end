package com.arextest.saas.api.model.contract.external;

import lombok.Data;

/**
 * @author b_yu
 * @since 2024/7/24
 */
@Data
public class InitSaasUserRequest {

  private String tenantCode;
  private String email;
  private String tenantToken;
  private Long expireTime;
}
