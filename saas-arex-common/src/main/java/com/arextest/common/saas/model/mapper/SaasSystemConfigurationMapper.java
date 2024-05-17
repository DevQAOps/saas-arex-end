package com.arextest.common.saas.model.mapper;

import com.arextest.common.saas.model.dao.SaasSystemConfigurationCollection;
import com.arextest.common.saas.model.dto.SaasSystemConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SaasSystemConfigurationMapper {

  SaasSystemConfigurationMapper INSTANCE = Mappers.getMapper(SaasSystemConfigurationMapper.class);

  SaasSystemConfiguration entityToDto(SaasSystemConfigurationCollection entity);

  SaasSystemConfigurationCollection dtoToEntity(SaasSystemConfiguration saasSystemConfiguration);
}
