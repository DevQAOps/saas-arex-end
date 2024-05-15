package com.arextest.common.saas.tenant;

import lombok.Data;

@Data
public class TenantStatusRedisInfo {

  private String tenantToken;

  private Long expireTime;

}