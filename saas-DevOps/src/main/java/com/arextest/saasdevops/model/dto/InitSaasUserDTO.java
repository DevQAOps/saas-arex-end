package com.arextest.saasdevops.model.dto;

import lombok.Data;

@Data
public class InitSaasUserDTO {
  private String email;
  private String tenantToken;
  private Integer status;
}
