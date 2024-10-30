package com.arextest.saas.api.repo.mapper;

import com.arextest.saas.api.model.contract.external.InitSaasUserRequest;
import com.arextest.saas.api.model.dao.TenantCollection;
import com.arextest.saas.api.model.dto.TenantDto;
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
