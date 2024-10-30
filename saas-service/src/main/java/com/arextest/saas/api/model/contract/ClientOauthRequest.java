package com.arextest.saas.api.model.contract;

import lombok.Data;

@Data
public class ClientOauthRequest {

  private String code;

  private Integer oauthType;
}
