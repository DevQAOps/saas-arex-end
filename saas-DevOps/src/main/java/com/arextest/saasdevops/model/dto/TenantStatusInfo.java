package com.arextest.saasdevops.model.dto;

import lombok.Data;

@Data
public class TenantStatusInfo {

  private String tenantCode;

  private String tenantToken;

  private Long expireTime;

}
