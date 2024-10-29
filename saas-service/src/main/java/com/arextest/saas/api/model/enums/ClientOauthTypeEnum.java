package com.arextest.saas.api.model.enums;

import lombok.Getter;

@Getter
public enum ClientOauthTypeEnum {
  OAUTH0(1, "oauth0");

  private Integer code;

  private String providerName;

  ClientOauthTypeEnum(Integer code, String providerName) {
    this.code = code;
    this.providerName = providerName;
  }
}