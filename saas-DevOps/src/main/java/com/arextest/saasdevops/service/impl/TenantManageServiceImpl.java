package com.arextest.saasdevops.service.impl;

import com.arextest.common.saas.tenant.TenantRedisHandler;
import com.arextest.common.saas.tenant.TenantStatusRedisInfo;
import com.arextest.saasdevops.mapper.TenantStatusMapper;
import com.arextest.saasdevops.model.dto.TenantStatusInfo;
import com.arextest.saasdevops.service.TenantManageService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TenantManageServiceImpl implements TenantManageService {

  @Resource
  TenantRedisHandler tenantRedisHandler;

  @Override
  public boolean initTenantStatus(TenantStatusInfo tenantStatusInfo) {
    String tenantCode = tenantStatusInfo.getTenantCode();
    if (StringUtils.isEmpty(tenantCode)) {
      return false;
    }
    TenantStatusRedisInfo tenantRedisInfo = TenantStatusMapper.INSTANCE.toTenantRedisInfo(
        tenantStatusInfo);
    return tenantRedisHandler.saveTenantStatusExpire(tenantCode, tenantRedisInfo);
  }


}
