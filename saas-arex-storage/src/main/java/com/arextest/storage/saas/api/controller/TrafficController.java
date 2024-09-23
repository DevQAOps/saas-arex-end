package com.arextest.storage.saas.api.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.model.replay.PagedRequestType;
import com.arextest.storage.saas.api.service.TrafficService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: QizhengMo
 * @date: 2024/3/13 13:02
 */
@Slf4j
@Controller
@RequestMapping("/api/traffic/")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class TrafficController {
  private final TrafficService trafficService;

  @PostMapping(value = "/summary", produces = "application/json")
  @ResponseBody
  public Response clearReplayPool(@RequestBody PagedRequestType req) {
    if (req.getPageSize() == 0) {
      req.setPageSize(20);
    }

    if (req.getPageIndex() == null || req.getPageIndex() < 1) {
      req.setPageIndex(1);
    }

    return ResponseUtils.successResponse(trafficService.trafficSummary(req));
  }
}
