package com.arextest.saas.api.model.dto.login;

import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/3/6 16:09
 */
@Data
public class OauthInfoDto {

  private String code;
  private String redirectUri;
}
