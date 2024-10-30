package com.arextest.saas.api.model.dto.login;

import lombok.Data;

@Data
public class Oauth0CodeInfo {
  private String email;
  private String providerUid;
  private long createdAt;
}