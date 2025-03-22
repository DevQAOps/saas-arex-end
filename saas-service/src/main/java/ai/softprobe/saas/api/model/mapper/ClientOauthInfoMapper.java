package ai.softprobe.saas.api.model.mapper;

import ai.softprobe.saas.api.model.contract.ClientOauthResponse;
import ai.softprobe.saas.api.model.contract.ClientOauthRequest;
import ai.softprobe.saas.api.model.dto.ClientOauthInfoDto;
import ai.softprobe.saas.api.model.dto.ClientOauthResultDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientOauthInfoMapper {

  ClientOauthInfoMapper INSTANCE = Mappers.getMapper(ClientOauthInfoMapper.class);

  ClientOauthInfoDto contractToDto(ClientOauthRequest contract);

  ClientOauthResponse dtoToResContract(ClientOauthResultDto dto);
}
