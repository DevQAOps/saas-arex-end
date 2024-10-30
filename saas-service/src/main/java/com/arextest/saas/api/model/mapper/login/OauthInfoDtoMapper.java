package com.arextest.saas.api.model.mapper.login;

import com.arextest.saas.api.model.contract.OauthLoginRequest;
import com.arextest.saas.api.model.dto.login.OauthInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OauthInfoDtoMapper {

  OauthInfoDtoMapper INSTANCE = Mappers.getMapper(OauthInfoDtoMapper.class);

  OauthInfoDto dtoFromContract(OauthLoginRequest request);

}
