package com.arextest.saasdevops.mapper;

import com.arextest.common.saas.tenant.TenantStatusRedisInfo;
import com.arextest.saasdevops.model.contract.InitSaasUserRequest;
import com.arextest.saasdevops.model.dto.TenantStatusInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TenantStatusMapper {

  TenantStatusMapper INSTANCE = Mappers.getMapper(TenantStatusMapper.class);

  TenantStatusInfo contractToDto(InitSaasUserRequest request);

  TenantStatusRedisInfo toTenantRedisInfo(TenantStatusInfo tenantStatusInfo);

}
