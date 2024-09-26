package com.arextest.storage.saas.api.service;

import com.arextest.config.model.dto.application.ApplicationConfiguration;
import com.arextest.config.model.dto.application.InstancesConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.model.replay.PagedRequestType;
import com.arextest.storage.saas.api.models.traffic.AppSummaryResponse;
import com.arextest.storage.saas.api.models.traffic.AppSummaryResponse.Endpoint;
import com.arextest.storage.saas.api.models.traffic.CaseSummaryRequest;
import com.arextest.storage.saas.api.models.traffic.TrafficAggregationResult;
import com.arextest.storage.saas.api.models.traffic.TrafficCase;
import com.arextest.storage.saas.api.models.traffic.TrafficSummaryResponse;
import com.arextest.storage.saas.api.models.traffic.TrafficSummaryResponse.TimeSeriesResult;
import com.arextest.storage.saas.api.repository.traffic.TrafficCalcRepository;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
  private final ConfigRepositoryProvider<InstancesConfiguration> instanceProvider;
  private final ConfigRepositoryProvider<ApplicationConfiguration> appProvider;

  private final TrafficCalcRepository trafficCalcRepository;
  private final TrafficRelationService trafficRelationService;

  public AppSummaryResponse appSummary(String appId) {
    AppSummaryResponse res = new AppSummaryResponse();
    List<Endpoint> endpoints = trafficRelationService.getEndpointsByAppId(appId);
    res.setInstances(instanceProvider.listBy(appId));
    res.setEndpoints(endpoints);
    res.setAppName(appProvider.listBy(appId).get(0).getAppName());
    return res;
  }

  public TrafficSummaryResponse trafficSummary(CaseSummaryRequest req) {
    TrafficSummaryResponse res = new TrafficSummaryResponse();
    Pair<Long, List<TrafficCase>> casesSummary = trafficCalcRepository.queryCaseBrief(req);
    List<TrafficCase> cases = casesSummary.getRight();

    res.setCases(cases);
    res.setTotal(casesSummary.getLeft());
    res.setTimeSeriesResult(calculateTimeSeries(req));
    return res;
  }

  private TimeSeriesResult calculateTimeSeries(CaseSummaryRequest req) {
    int step;
    long range = req.getEndTime() - req.getBeginTime();
    if (range <= 1000L * 60 * 30) {
      step = 1000 * 10;
    } else if (range <= 1000L * 60 * 60) {
      step = 1000 * 30;
    } else if (range <= 1000L * 60 * 60 * 6) {
      step = 1000 * 60 * 5;
    } else {
      step = 1000 * 60 * 10;
    }

    List<TrafficAggregationResult> countByTimeShards = trafficCalcRepository.countCasesByRange(req, step);

    TimeSeriesResult timeSeries = new TimeSeriesResult();
    timeSeries.setFrom(req.getBeginTime());
    timeSeries.setTo(req.getEndTime());
    timeSeries.setStep(step);
    timeSeries.setShards(countByTimeShards.stream().collect(Collectors.toMap(TrafficAggregationResult::getSeq, TrafficAggregationResult::getCount)));
    return timeSeries;
  }
}
