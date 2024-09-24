package com.arextest.storage.saas.api.models.traffic;

import com.arextest.config.model.dto.application.InstancesConfiguration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;

/**
 * @author: QizhengMo
 * @date: 2024/9/19 19:53
 */
@Data
public class AppSummaryResponse {
  private String appName;
  private List<InstancesConfiguration> instances;
  // traffic dependency summary
  private List<Endpoint> endpoints;

  @Data
  public static class Endpoint {
    // eg: /api/xxx
    private String endpoint;
    // eg: HTTP
    private String type;
    private String operationId;
    private Set<Dependency> dependencies = new HashSet<>();
  }

  @Data
  public static class Dependency {
    private String type;
    private String operation;
  }
}
