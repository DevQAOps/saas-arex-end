package ai.softprobe.saas.common.model.mapper;

import ai.softprobe.saas.common.model.dto.SaasSystemConfiguration;
import ai.softprobe.saas.common.model.dao.SaasSystemConfigurationCollection;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SaasSystemConfigurationMapper {

  SaasSystemConfigurationMapper INSTANCE = Mappers.getMapper(SaasSystemConfigurationMapper.class);

  SaasSystemConfiguration entityToDto(SaasSystemConfigurationCollection entity);

  SaasSystemConfigurationCollection dtoToEntity(SaasSystemConfiguration saasSystemConfiguration);
}
