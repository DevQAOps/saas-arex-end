package com.arextest.saas.api.service;

import com.arextest.saas.api.model.contract.QueryUsageRequest;
import com.arextest.saas.api.model.contract.SubscribePlanRequest;
import com.arextest.saas.api.model.dto.TenantDto;
import com.arextest.saas.api.model.dto.UsageInfo;

public interface SubscribeService {

  boolean subscribePlan(String tenantCode, SubscribePlanRequest request);

  boolean initDataBySubscription(TenantDto tenantDto);

  UsageInfo getUsageInfo(String tenantCode, QueryUsageRequest request);
}
