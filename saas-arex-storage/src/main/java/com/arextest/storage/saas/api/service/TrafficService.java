package com.arextest.storage.saas.api.service;

import com.arextest.model.replay.PagedRequestType;
import com.arextest.storage.saas.api.models.traffic.TrafficCase;
import com.arextest.storage.saas.api.models.traffic.TrafficSummaryResponse;
import com.arextest.storage.saas.api.repository.traffic.TrafficCalcRepository;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author: QizhengMo
 * @date: 2024/9/19 20:10
 */
@RequiredArgsConstructor
@Service
public class TrafficService {
  private final TrafficCalcRepository trafficCalcRepository;

  public TrafficSummaryResponse trafficSummary(PagedRequestType req) {
    TrafficSummaryResponse res = new TrafficSummaryResponse();
    Date from = new Date(req.getBeginTime());
    Date to = new Date(req.getEndTime());

    List<TrafficCase> cases = trafficCalcRepository.queryCaseBrief(from, to,
        req.getPageSize(), (req.getPageIndex() - 1) * req.getPageSize());

    res.setCases(cases);
    return res;
  }
}
