package com.arextest.web.saas.model.contract;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaasVerifyRequestType {
  @NotBlank(message = "ticket cannot be empty")
  private String ticket;
}
