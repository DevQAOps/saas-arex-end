package com.arextest.storage.saas.api.models.traffic;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author: QizhengMo
 * @date: 2024/9/25 13:57
 */
@Data
public class TrafficAggregationResult {
  @Id
  private Integer seq;
  private Long count;
}
