package com.arextest.saasdevops.service;

import com.arextest.saasdevops.model.contract.QueryTenantUsageRequest;
import com.arextest.saasdevops.model.contract.UpdateSubScribeRequest;

/**
 * @author wildeslam.
 * @create 2024/6/17 16:28
 */
public interface UsageService {

  Long queryUsage(QueryTenantUsageRequest request);

  boolean updateSubScribe(UpdateSubScribeRequest request);
}
