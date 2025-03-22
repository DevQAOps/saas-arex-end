package ai.softprobe.saas.api.service;

import ai.softprobe.saas.api.model.contract.QueryUsageRequest;
import ai.softprobe.saas.api.model.contract.SubscribePlanRequest;
import ai.softprobe.saas.api.model.dto.TenantDto;
import ai.softprobe.saas.api.model.dto.UsageInfo;

public interface SubscribeService {

  boolean subscribePlan(String tenantCode, SubscribePlanRequest request);

  boolean initDataBySubscription(TenantDto tenantDto);

  UsageInfo getUsageInfo(String tenantCode, QueryUsageRequest request);
}
