package com.arextest.saas.api.model.mapper;

import com.arextest.saas.api.model.contract.ClientOauthResponse;
import com.arextest.saas.api.model.contract.ClientOauthRequest;
import com.arextest.saas.api.model.dto.ClientOauthInfoDto;
import com.arextest.saas.api.model.dto.ClientOauthResultDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientOauthInfoMapper {

  ClientOauthInfoMapper INSTANCE = Mappers.getMapper(ClientOauthInfoMapper.class);

  ClientOauthInfoDto contractToDto(ClientOauthRequest contract);

  ClientOauthResponse dtoToResContract(ClientOauthResultDto dto);
}
