package ai.softprobe.saas.devops.service.impl;

import ai.softprobe.saas.common.tenant.TenantRedisHandler;
import ai.softprobe.saas.common.tenant.TenantStatusRedisInfo;
import ai.softprobe.saas.devops.mapper.TenantStatusMapper;
import ai.softprobe.saas.devops.model.dto.TenantStatusInfo;
import ai.softprobe.saas.devops.service.TenantManageService;
import jakarta.annotation.Resource;
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
