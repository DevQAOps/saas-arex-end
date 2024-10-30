package com.arextest.saas.api.model.dto;

import java.util.Map;
import lombok.Data;

@Data
public class ClientOauthResultDto {

  private String email;
  private boolean emailVerified;
  private Map<String, String> providerProfile;
}
