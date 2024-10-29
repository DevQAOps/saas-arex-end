package com.arextest.saas.api.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.saas.api.common.utils.CommonUtil;
import com.arextest.saas.api.common.utils.JwtUtil;
import com.arextest.saas.api.service.SubscribeService;
import com.arextest.saas.api.model.contract.QueryUsageRequest;
import com.arextest.saas.api.model.contract.QueryUsageResponse;
import com.arextest.saas.api.model.contract.SubscribePlanRequest;
import com.arextest.saas.api.model.dto.UsageInfo;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * subscribe package plan
 */
@Slf4j
@Controller
@RequestMapping("/api/subscribe")
public class SubscribeController {

  @Resource
  SubscribeService subscribeService;

  @PostMapping("/subscribePlan")
  @ResponseBody
  public Response subscribePlan(@RequestHeader("access-token") String accessToken,
      @RequestBody SubscribePlanRequest request) {
    String tenantCode = JwtUtil.getUserNameByUserToken(accessToken);
    CommonUtil.checkTenantCode(tenantCode);
    return ResponseUtils.successResponse(subscribeService.subscribePlan(tenantCode, request));
  }


  @PostMapping("/queryUsage")
  @ResponseBody
  public Response queryUsage(@RequestHeader(name = "access-token") String accessToken,
      @Valid @RequestBody QueryUsageRequest request) {
    String tenantCode = JwtUtil.getUserNameByUserToken(accessToken);
    CommonUtil.checkTenantCode(tenantCode);
    QueryUsageResponse responseType = new QueryUsageResponse();

    UsageInfo usageInfo = subscribeService.getUsageInfo(tenantCode, request);
    responseType.setSuccess(true);
    responseType.setMemberLimit(usageInfo.getMemberLimit());
    responseType.setMemberUsage(usageInfo.getMemberUsage());
    responseType.setTrafficUsageBytes(usageInfo.getTrafficUsage());
    responseType.setTrafficLimitBytes(usageInfo.getTrafficLimit());

    return ResponseUtils.successResponse(responseType);
  }
}
