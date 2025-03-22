package ai.softprobe.saas.api.repo.mapper;

import ai.softprobe.saas.api.model.contract.external.InitSaasUserRequest;
import ai.softprobe.saas.api.model.dao.TenantCollection;
import ai.softprobe.saas.api.model.dto.TenantDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author wildeslam.
 * @create 2024/3/6 13:59
 */
@Mapper
public interface UserMapper {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  TenantDto dtoFromDao(TenantCollection dao);

  TenantCollection daoFromDto(TenantDto dto);

  InitSaasUserRequest toInitSaasUserRequest(TenantDto dto);

}
