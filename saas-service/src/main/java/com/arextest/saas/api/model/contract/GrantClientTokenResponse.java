package com.arextest.saas.api.model.contract;

import lombok.Data;

@Data
public class GrantClientTokenResponse {

  private String accessToken;

  private String refreshToken;
}
