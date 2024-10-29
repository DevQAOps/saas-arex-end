package com.arextest.saas.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wildeslam.
 * @create 2024/3/6 14:32
 */
@Getter
@AllArgsConstructor
public enum TenantLevelEnum {

  DISABLED(-1),
  EXPIRED(0),
  NORMAL(1)
  ;

  private int code;
}
