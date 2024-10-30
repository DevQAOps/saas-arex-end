package com.arextest.saas.api.service;

import com.arextest.saas.api.model.dto.ClientOauthInfoDto;
import com.arextest.saas.api.model.dto.ClientOauthResultDto;
import com.arextest.saas.api.model.enums.ClientOauthTypeEnum;

public interface ClientOauthService {

  ClientOauthTypeEnum supportOauthType();

  ClientOauthResultDto doOauth(ClientOauthInfoDto clientOauthInfoDto);

}
