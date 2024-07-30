package com.arextest.common.saas.multitenant.usage;

import lombok.Builder;
import lombok.Data;

/**
 * @author: QizhengMo
 * @date: 2024/6/11 13:59
 */
@Data
@Builder
public class UsageCacheKey {

  private String tenant;
  private String endpoint;
  private boolean in;
}
