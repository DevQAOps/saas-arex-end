package com.arextest.saas.api.model.contract;

import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/3/6 20:47
 */
@Data
public class OauthLoginResponse extends LoginResponse {

  private Boolean needBind;
  private String email;
}
