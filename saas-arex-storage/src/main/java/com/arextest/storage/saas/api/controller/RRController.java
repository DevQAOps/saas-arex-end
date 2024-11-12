package com.arextest.storage.saas.api.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.storage.saas.api.models.rr.RecordListingReq;
import com.arextest.storage.saas.api.models.rr.RecordingReq;
import com.arextest.storage.saas.api.service.RRService;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: QizhengMo
 * @date: 2024/11/12 10:58
 */
@Controller
@RequestMapping(path = "/api/rr", produces = "application/json")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class RRController {
  private final RRService rrService;

  @PostMapping("/record")
  @ResponseBody
  public Response record(@RequestBody RecordingReq req) {
    return ResponseUtils.successResponse(rrService.record(req));
  }

  @PostMapping("/list")
  @ResponseBody
  public Response list(@RequestBody RecordListingReq req) {
    if (req.getFrom() == null) {
      req.setFrom(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
    }
    if (req.getTo() == null) {
      req.setTo(new Date());
    }
    return ResponseUtils.successResponse(rrService.listRecords(req));
  }

  @GetMapping("/detail/{recordId}")
  @ResponseBody
  public Response detail(@PathVariable String recordId) {
    return ResponseUtils.successResponse(rrService.detail(recordId));
  }
}
