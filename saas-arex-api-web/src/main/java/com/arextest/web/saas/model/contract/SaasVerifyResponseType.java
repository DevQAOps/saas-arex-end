package com.arextest.web.saas.model.contract;

import lombok.Data;

@Data
public class SaasVerifyResponseType {
  private String accessToken;
  private String refreshToken;
}
