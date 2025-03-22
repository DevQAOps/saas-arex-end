package ai.softprobe.saas.api.service.impl;

import ai.softprobe.saas.api.common.enums.ErrorCode;
import ai.softprobe.saas.api.common.exceptions.SpSaasException;
import ai.softprobe.saas.api.repo.TenantRepository;
import ai.softprobe.saas.api.repo.mapper.QueryUsageMapper;
import ai.softprobe.saas.api.repo.mapper.UserMapper;
import ai.softprobe.saas.api.service.DevopsServiceHandler;
import ai.softprobe.saas.api.service.SubscribeService;
import ai.softprobe.saas.api.model.contract.QueryUsageRequest;
import ai.softprobe.saas.api.model.contract.SubscribePlanRequest;
import ai.softprobe.saas.api.model.contract.external.InitSaasUserRequest;
import ai.softprobe.saas.api.model.contract.external.QueryTenantUsageRequest;
import ai.softprobe.saas.api.model.dto.TenantDto;
import ai.softprobe.saas.api.model.dto.UsageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SubscribeServiceImpl implements SubscribeService {

  @Resource
  TenantRepository tenantRepository;

  @Resource
  DevopsServiceHandler devopsServiceHandler;

  @Override
  public boolean subscribePlan(String tenantCode, SubscribePlanRequest request) {
    TenantDto tenantDto = tenantRepository.queryTenant(tenantCode);
    if (tenantDto == null) {
      throw new SpSaasException(ErrorCode.TENANT_NOT_EXISTED.getCodeValue(), "User not found");
    }

    return updateDataBySubscription(tenantDto);
  }

  public boolean initDataBySubscription(TenantDto tenantDto) {

    // call devops to initialize resources
    InitSaasUserRequest initSaasUserRequest = UserMapper.INSTANCE.toInitSaasUserRequest(tenantDto);
    devopsServiceHandler.initialUserRepo(initSaasUserRequest);

    //update traffic limit
    devopsServiceHandler.updateSubscribe(tenantDto.getTenantCode(),
        tenantDto.getTrafficLimit(), tenantDto.getPackageEffectiveTime(),
        tenantDto.getExpireTime());
    return true;
  }

  @Override
  public UsageInfo getUsageInfo(String tenantCode, QueryUsageRequest queryUsageRequest) {
    UsageInfo usageInfo = new UsageInfo();
    TenantDto tenantDto = tenantRepository.queryTenant(tenantCode);
    if (tenantDto == null) {
      throw new SpSaasException(ErrorCode.TENANT_NOT_EXISTED.getCodeValue(), "Tenant not found");
    }
    if (tenantDto.getUserInfos() != null) {
      usageInfo.setMemberUsage(tenantDto.getUserInfos().size());
    }
    usageInfo.setMemberLimit(tenantDto.getMemberLimit());
    usageInfo.setTrafficLimit(tenantDto.getTrafficLimit());

    queryUsageRequest.setStartTime(tenantDto.getPackageEffectiveTime());
    queryUsageRequest.setEndTime(tenantDto.getExpireTime());

    QueryTenantUsageRequest request = QueryUsageMapper.INSTANCE.toQueryTenantUsageRequest(
        queryUsageRequest);
    usageInfo.setTrafficUsage(devopsServiceHandler.queryTrafficUsage(request));
    return usageInfo;
  }

  private boolean updateDataBySubscription(TenantDto tenantDto) {
    return false;
  }
}
