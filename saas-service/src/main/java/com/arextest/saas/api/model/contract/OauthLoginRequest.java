package com.arextest.saas.api.model.contract;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/3/6 20:47
 */
@Data
public class OauthLoginRequest {
  @NotNull
  private Integer oauthType;

  private String code;
  private String redirectUri;
}
