package com.arextest.storage.saas.api.models.traffic;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author: QizhengMo
 * @date: 2024/9/19 19:53
 */
@Data
public class TrafficSummaryResponse {
  private List<TrafficCase> cases;
  private Long total;

  private TimeSeriesResult timeSeriesResult;

  @Data
  public static class TimeSeriesResult {
    private Long from;
    private Long to;
    private Integer step;
    private Map<Integer, Long> shards;
  }

  @Data
  public static class Dependency {
    private String type;
    private String operation;
  }
}
