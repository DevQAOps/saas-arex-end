package com.arextest.saasdevops.service.impl;

import com.arextest.common.saas.model.TenantUsageDocument;
import com.arextest.common.saas.repository.impl.UsageStatDao;
import com.arextest.saasdevops.service.UsageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author wildeslam.
 * @create 2024/6/17 16:32
 */
@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {
  private final UsageStatDao usageStatDao;

  @Override
  public Long queryUsage(String tenantCode, Boolean in) {
    List<TenantUsageDocument> tenantUsageDocuments = usageStatDao.query(tenantCode, in);
    Long sum = 0L;
    for (TenantUsageDocument tenantUsageDocument : tenantUsageDocuments) {
      sum += tenantUsageDocument.getContentLengthSum();
    }
    return sum;
  }
}
