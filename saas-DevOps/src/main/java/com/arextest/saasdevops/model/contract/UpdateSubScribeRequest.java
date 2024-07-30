package com.arextest.saasdevops.model.contract;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wildeslam.
 * @create 2024/7/9 16:10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateSubScribeRequest extends BaseRequest {

  private Long trafficLimit;
  private Long start;
  private Long end;
}
