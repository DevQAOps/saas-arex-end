package com.arextest.saas.api.service.impl;

import com.arextest.saas.api.common.enums.ErrorCode;
import com.arextest.saas.api.common.exceptions.ArexSaasException;
import com.arextest.saas.api.repo.TenantRepository;
import com.arextest.saas.api.repo.mapper.QueryUsageMapper;
import com.arextest.saas.api.repo.mapper.UserMapper;
import com.arextest.saas.api.service.DevopsServiceHandler;
import com.arextest.saas.api.service.SubscribeService;
import com.arextest.saas.api.model.contract.QueryUsageRequest;
import com.arextest.saas.api.model.contract.SubscribePlanRequest;
import com.arextest.saas.api.model.contract.external.InitSaasUserRequest;
import com.arextest.saas.api.model.contract.external.QueryTenantUsageRequest;
import com.arextest.saas.api.model.dto.TenantDto;
import com.arextest.saas.api.model.dto.UsageInfo;
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
      throw new ArexSaasException(ErrorCode.TENANT_NOT_EXISTED.getCodeValue(), "User not found");
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
      throw new ArexSaasException(ErrorCode.TENANT_NOT_EXISTED.getCodeValue(), "Tenant not found");
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
