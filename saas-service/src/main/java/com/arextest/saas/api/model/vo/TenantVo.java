package com.arextest.saas.api.model.vo;

import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/4/2 19:55
 */
@Data
public class TenantVo {

  private String email;
  private String tenantName;
  private String tenantCode;
  private String phoneNumber;
  private String profile;
  private Integer userLevel;
  private Long expireTime;
  private String tenantToken;
  /**
   * @see com.arextest.saas.common.enums.TenantStatus
   */
  private Integer tenantStatus;
}
