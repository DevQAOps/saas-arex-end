package ai.softprobe.saas.devops.mapper;

import ai.softprobe.saas.common.tenant.TenantStatusRedisInfo;
import ai.softprobe.saas.devops.model.dto.TenantStatusInfo;
import ai.softprobe.saas.devops.model.contract.InitSaasUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TenantStatusMapper {

  TenantStatusMapper INSTANCE = Mappers.getMapper(TenantStatusMapper.class);

  TenantStatusInfo contractToDto(InitSaasUserRequest request);

  TenantStatusRedisInfo toTenantRedisInfo(TenantStatusInfo tenantStatusInfo);

}
