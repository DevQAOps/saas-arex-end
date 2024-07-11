package com.arextest.saasdevops.service.impl;

import com.arextest.common.saas.model.SaasSystemConfigurationKeySummary;
import com.arextest.common.saas.model.TenantUsageDocument;
import com.arextest.common.saas.model.dao.SaasSystemConfigurationCollection.SubscribeInfo;
import com.arextest.common.saas.model.dto.SaasSystemConfiguration;
import com.arextest.common.saas.repository.SaasSystemConfigurationRepository;
import com.arextest.common.saas.repository.impl.UsageStatDao;
import com.arextest.saasdevops.model.contract.QueryTenantUsageRequest;
import com.arextest.saasdevops.model.contract.UpdateSubScribeRequest;
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
  private final SaasSystemConfigurationRepository saasSystemConfigurationRepository;

  @Override
  public Long queryUsage(QueryTenantUsageRequest request) {
    List<TenantUsageDocument> tenantUsageDocuments = usageStatDao.query(
        request.getTenantCode(), request.getIn(), request.getStartTime(), request.getEndTime());
    Long sum = 0L;
    for (TenantUsageDocument tenantUsageDocument : tenantUsageDocuments) {
      sum += tenantUsageDocument.getContentLengthSum();
    }
    return sum;
  }

  @Override
  public boolean updateSubScribe(UpdateSubScribeRequest request) {
    if (request.getTrafficLimit() == null && request.getStart() == null
        && request.getEnd() == null) {
      return false;
    }
    SaasSystemConfiguration subscribeConfig = new SaasSystemConfiguration();
    subscribeConfig.setKey(SaasSystemConfigurationKeySummary.SAAS_SUBSCRIBE_INFO);
    subscribeConfig.setSubscribeInfo(
        new SubscribeInfo(request.getTrafficLimit(), request.getStart(), request.getEnd()));
    return saasSystemConfigurationRepository.save(subscribeConfig);
  }
}
