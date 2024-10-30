package com.arextest.saas.api.model.contract;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wildeslam.
 * @create 2024/6/18 16:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryUsageResponse extends SuccessResponseType {

  private Long trafficUsageBytes;
  private Long trafficLimitBytes;

  private Integer memberUsage;
  private Integer memberLimit;
}
