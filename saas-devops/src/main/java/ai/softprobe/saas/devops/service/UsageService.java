package ai.softprobe.saas.devops.service;

import ai.softprobe.saas.devops.model.contract.UpdateSubScribeRequest;
import ai.softprobe.saas.devops.model.contract.QueryTenantUsageRequest;

/**
 * @author wildeslam.
 * @create 2024/6/17 16:28
 */
public interface UsageService {

  Long queryUsage(QueryTenantUsageRequest request);

  boolean updateSubScribe(UpdateSubScribeRequest request);
}
