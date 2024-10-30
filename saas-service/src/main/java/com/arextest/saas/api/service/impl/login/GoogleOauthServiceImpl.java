package com.arextest.saas.api.service.impl.login;

import com.arextest.saas.api.service.OauthService;
import com.arextest.saas.api.model.dto.login.OauthInfoDto;
import com.arextest.saas.api.model.dto.login.OauthResult;
import com.arextest.saas.api.model.enums.OauthTypeEnum;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author wildeslam.
 * @create 2024/3/6 20:32
 */
@Deprecated
@Slf4j
@Service
public class GoogleOauthServiceImpl implements OauthService {

  private static final String SCOPE = "https://www.googleapis.com/auth/userinfo.email";
  private static final String OFFLINE = "offline";
  @Value("${arex.oauth.google.clientid}")
  private String clientId;
  @Value("${arex.oauth.google.secret}")
  private String secret;

  @Override
  public OauthTypeEnum getOauthType() {
    return OauthTypeEnum.GOOGLE;
  }


  private boolean checkOauth(String clientId, String secret, OauthInfoDto oauthInfoDto) {
    if (StringUtils.isBlank(clientId)) {
      LOGGER.error("Oauth clientId is null");
      return false;
    }
    if (StringUtils.isBlank(secret)) {
      LOGGER.error("Oauth secret is null");
      return false;
    }

    if (oauthInfoDto == null) {
      LOGGER.error("Oauth code is null");
      return false;
    }

    if (StringUtils.isBlank(oauthInfoDto.getCode())) {
      LOGGER.error("Oauth code is null");
      return false;
    }

    if (StringUtils.isBlank(oauthInfoDto.getRedirectUri())) {
      LOGGER.error("google redirect uri is blank");
      return false;
    }
    return true;
  }

  @Override
  public OauthResult oauth(OauthInfoDto oauthInfoDto) throws Exception {

    if (!checkOauth(clientId, secret, oauthInfoDto)) {
      return null;
    }

    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    GoogleAuthorizationCodeFlow flow = new Builder(httpTransport,
        jsonFactory,
        clientId, secret, Collections.singleton(SCOPE)).setAccessType(OFFLINE).build();

    GoogleAuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(
        oauthInfoDto.getCode());
    tokenRequest.setRedirectUri(oauthInfoDto.getRedirectUri());
    GoogleTokenResponse tokenResponse = tokenRequest.execute();

    GoogleIdToken token = null;
    if (StringUtils.isNotBlank(tokenResponse.getIdToken())) {
      GoogleIdTokenVerifier verifier =
          new GoogleIdTokenVerifier.Builder(flow.getTransport(), flow.getJsonFactory()).build();

      token = verifier.verify(tokenResponse.getIdToken());
    }
    if (token != null) {
      OauthResult oauthResult = new OauthResult();
      Payload payload = token.getPayload();
      String email = payload.getEmail();
      if (StringUtils.isEmpty(email)) {
        throw new RuntimeException("failed to get email from oauth service");
      }
      oauthResult.setEmail(payload.getEmail());
      return oauthResult;
    }
    return null;
  }
}
