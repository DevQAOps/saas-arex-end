package com.arextest.storage.saas.api.service;

import com.arextest.config.model.dto.application.InstancesConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.model.mock.MockCategoryType;
import com.arextest.model.replay.PagedRequestType;
import com.arextest.storage.saas.api.models.traffic.AppSummaryResponse;
import com.arextest.storage.saas.api.models.traffic.AppSummaryResponse.Endpoint;
import com.arextest.storage.saas.api.models.traffic.TrafficCase;
import com.arextest.storage.saas.api.models.traffic.TrafficSummaryResponse;
import com.arextest.storage.saas.api.repository.traffic.TrafficCalcRepository;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

/**
 * @author: QizhengMo
 * @date: 2024/9/19 20:10
 */
@RequiredArgsConstructor
@Service
public class TrafficService {
  private final ConfigRepositoryProvider<InstancesConfiguration> instanceProvider;
  private final TrafficCalcRepository trafficCalcRepository;
  private final TrafficRelationService trafficRelationService;
  private final Set<MockCategoryType> entryPointTypes;

  public AppSummaryResponse appSummary(String appId) {
    AppSummaryResponse res = new AppSummaryResponse();
    List<Endpoint> endpoints = trafficRelationService.getEndpointsByAppId(appId);
    res.setInstances(instanceProvider.listBy(appId));
    res.setEndpoints(endpoints);
    return res;
  }

  public TrafficSummaryResponse trafficSummary(PagedRequestType req) {
    TrafficSummaryResponse res = new TrafficSummaryResponse();
    Date from = new Date(req.getBeginTime());
    Date to = new Date(req.getEndTime());

    Pair<Long, List<TrafficCase>> casesSummary = trafficCalcRepository.queryCaseBrief(Collections.singleton(req.getCategory()),
        req.getAppId(), from, to, req.getPageSize(), (req.getPageIndex() - 1) * req.getPageSize());
    List<TrafficCase> cases = casesSummary.getRight();

    res.setCases(cases);
    res.setTotal(casesSummary.getLeft());
    return res;
  }
}
