package com.arextest.saas.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wildeslam.
 * @create 2024/3/6 16:11
 */
@Getter
@AllArgsConstructor
public enum OauthTypeEnum {
  @Deprecated
  GOOGLE(1),
  @Deprecated
  FACEBOOK(2),
  @Deprecated
  GITHUB(3),
  OAUTH0(4);

  private Integer code;
}
