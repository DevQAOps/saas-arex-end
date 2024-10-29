package com.arextest.saas.api.model.dto;

import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/6/18 17:09
 */
@Data
public class UsageInfo {
  private Long trafficLimit;
  private Integer memberLimit;
  private Long trafficUsage;
  private Integer memberUsage;
}
