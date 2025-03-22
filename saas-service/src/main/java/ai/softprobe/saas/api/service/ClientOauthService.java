package ai.softprobe.saas.api.service;

import ai.softprobe.saas.api.model.dto.ClientOauthInfoDto;
import ai.softprobe.saas.api.model.dto.ClientOauthResultDto;
import ai.softprobe.saas.api.model.enums.ClientOauthTypeEnum;

public interface ClientOauthService {

  ClientOauthTypeEnum supportOauthType();

  ClientOauthResultDto doOauth(ClientOauthInfoDto clientOauthInfoDto);

}
