package com.arextest.storage.saas.api.service;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.model.dto.application.ApplicationServiceConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.storage.saas.api.models.traffic.AppSummaryResponse.Endpoint;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author: QizhengMo
 * @date: 2024/9/23 17:15
 */
@Service
@RequiredArgsConstructor
public class TrafficRelationService {
  private final ConfigRepositoryProvider<ApplicationServiceConfiguration> endpointProvider;

  public List<Endpoint> getEndpointsByAppId(String appId) {
    List<ApplicationServiceConfiguration> services = endpointProvider.listBy(appId);
    List<Endpoint> res = new ArrayList<>();
    for (ApplicationServiceConfiguration service : services) {
      for (ApplicationOperationConfiguration endpoint : service.getOperationList()) {
        for (String type : endpoint.getOperationTypes()) {
          Endpoint endpointDto = new Endpoint();
          endpointDto.setEndpoint(endpoint.getOperationName());
          endpointDto.setType(type);
          res.add(endpointDto);
        }
      }
    }
    return res;
  }
}
