package com.arextest.saas.api.service;

import com.arextest.saas.api.model.dto.login.OauthInfoDto;
import com.arextest.saas.api.model.dto.login.OauthResult;
import com.arextest.saas.api.model.enums.OauthTypeEnum;

/**
 * @author wildeslam.
 * @create 2024/3/5 16:15
 */
public interface OauthService {

  OauthTypeEnum getOauthType();

  OauthResult oauth(OauthInfoDto oauthInfoDto) throws Exception;


}
