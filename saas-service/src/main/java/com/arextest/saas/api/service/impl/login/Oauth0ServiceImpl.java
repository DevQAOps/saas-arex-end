package com.arextest.saas.api.service.impl.login;

import com.arextest.saas.api.service.OauthService;
import com.arextest.saas.api.model.dto.login.Oauth0CodeInfo;
import com.arextest.saas.api.model.dto.login.OauthInfoDto;
import com.arextest.saas.api.model.dto.login.OauthResult;
import com.arextest.saas.api.model.enums.OauthTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Oauth0ServiceImpl implements OauthService, InitializingBean {

  @Value("${arex.oauth.oauth0.rsaPrivateKey}")
  private String rsaPrivateKey;

  @Resource
  private ObjectMapper objectMapper;

  private Cipher cipher;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(rsaPrivateKey));
  }


  @Override
  public OauthTypeEnum getOauthType() {
    return OauthTypeEnum.OAUTH0;
  }

  @Override
  public OauthResult oauth(OauthInfoDto oauthInfoDto) throws Exception {

    if (!checkOauth(oauthInfoDto)) {
      return null;
    }

    Oauth0CodeInfo oauth0CodeInfo = getOauth0CodeInfo(oauthInfoDto.getCode());
    if (System.currentTimeMillis() - oauth0CodeInfo.getCreatedAt() > 5 * 60 * 1000) {
      throw new RuntimeException("Oauth0 code expired");
    }

    if (StringUtils.isEmpty(oauth0CodeInfo.getEmail())) {
      throw new RuntimeException("failed to get email from oauth0 code");
    }
    OauthResult oauthResult = new OauthResult();
    oauthResult.setEmail(oauth0CodeInfo.getEmail());
    oauthResult.setProviderUid(oauth0CodeInfo.getProviderUid());
    return oauthResult;
  }


  private boolean checkOauth(OauthInfoDto oauthInfoDto) {

    if (oauthInfoDto == null) {
      LOGGER.error("Oauth code is null");
      return false;
    }

    if (StringUtils.isEmpty(oauthInfoDto.getCode())) {
      LOGGER.error("Oauth code is null");
      return false;
    }
    return true;
  }


  private Oauth0CodeInfo getOauth0CodeInfo(String encryptedData)
      throws Exception {
    byte[] decode = Base64.getDecoder().decode(encryptedData);
    byte[] decryptedData = cipher.doFinal(decode);
    String decryptedMessage = new String(decryptedData);
    return objectMapper.readValue(decryptedMessage, Oauth0CodeInfo.class);
  }


  private PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
    byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePrivate(keySpec);
  }


}
