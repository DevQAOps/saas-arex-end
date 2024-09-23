package com.arextest.storage.saas.api.models.traffic;

import com.arextest.model.response.Response;
import com.arextest.model.response.ResponseStatusType;
import java.util.List;
import java.util.Set;
import lombok.Data;

/**
 * @author: QizhengMo
 * @date: 2024/9/19 19:53
 */
@Data
public class TrafficSummaryResponse {
  // traffic dependency summary
  private List<Endpoint> endpoints;

  // traffic distribution over time
  private List<Shard> shards;

  private List<TrafficCase> cases;
  private Long total;


  @Data
  public static class Shard {
    private String time;
    private Long count;
  }

  @Data
  public static class Endpoint {
    // eg: /api/xxx
    private String endpoint;
    // eg: HTTP
    private String type;
    private Set<Dependency> dependencies;
  }

  @Data
  public static class Dependency {
    private String type;
    private String operation;
  }
}
