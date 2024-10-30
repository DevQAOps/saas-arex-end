package com.arextest.saas.api.service.impl;

import com.arextest.saas.api.service.CompanyTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CompanyTokenServiceImpl implements CompanyTokenService {

  private static final String SPLIT_SYMBOL = ":";
  @Value("${arex.agent.aesKey:}")
  private String aesKey;
  @Resource
  private ObjectMapper objectMapper;

  @Override
  public String generateToken(String tenantCode) throws Exception {
    CompanyToken companyToken = new CompanyToken();
    companyToken.setTenantCode(tenantCode);
    companyToken.setCreateTime(System.currentTimeMillis());
    String tokenJson = objectMapper.writeValueAsString(companyToken);
    String encryptTokenJson = encrypt(tokenJson);
    return subString(tenantCode, encryptTokenJson);
  }


  public String encrypt(String s) throws Exception {
    byte[] contentBytes = s.getBytes();
    byte[] aesKeyBytes = Base64.getDecoder().decode(aesKey.getBytes());
    byte[] encryptedContent = encryptAES(contentBytes, aesKeyBytes);
    return Base64.getEncoder().encodeToString(encryptedContent);
  }

  /**
   * encrypt
   *
   * @throws Exception
   */
  private byte[] encryptAES(byte[] data, byte[] key) throws Exception {
    SecretKey secretKey = new SecretKeySpec(key, "AES");
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    return cipher.doFinal(data);
  }

  public String subString(String tenantCode, String tokenJson) {
    return tenantCode + SPLIT_SYMBOL + tokenJson;
  }


  @Data
  private static class CompanyToken {

    private String tenantCode;
    private long createTime;
  }


}
