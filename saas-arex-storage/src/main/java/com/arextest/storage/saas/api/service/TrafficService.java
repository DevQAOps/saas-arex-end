package com.arextest.storage.saas.api.service;

import com.arextest.model.mock.MockCategoryType;
import com.arextest.model.replay.PagedRequestType;
import com.arextest.storage.saas.api.models.traffic.TrafficCase;
import com.arextest.storage.saas.api.models.traffic.TrafficSummaryResponse;
import com.arextest.storage.saas.api.models.traffic.TrafficSummaryResponse.Endpoint;
import com.arextest.storage.saas.api.repository.traffic.TrafficCalcRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
  private final TrafficCalcRepository trafficCalcRepository;
  private final TrafficRelationService trafficRelationService;
  private final Set<MockCategoryType> entryPointTypes;

  public TrafficSummaryResponse trafficSummary(PagedRequestType req) {
    TrafficSummaryResponse res = new TrafficSummaryResponse();
    Date from = new Date(req.getBeginTime());
    Date to = new Date(req.getEndTime());

    List<Endpoint> endpoints = trafficRelationService.getEndpointsByAppId(req.getAppId());
    Set<String> appEntryTypeNames = endpoints.stream().map(Endpoint::getType).collect(Collectors.toSet());
    Set<MockCategoryType> appEntryTypes = entryPointTypes.stream()
        .filter(e -> appEntryTypeNames.contains(e.getName())).collect(Collectors.toSet());

    Pair<Long, List<TrafficCase>> casesSummary = trafficCalcRepository.queryCaseBrief(appEntryTypes,
        req.getAppId(), from, to, req.getPageSize(), (req.getPageIndex() - 1) * req.getPageSize());
    List<TrafficCase> cases = casesSummary.getRight();

    res.setCases(cases);
    res.setTotal(casesSummary.getLeft());
    res.setEndpoints(endpoints);
    return res;
  }
}
