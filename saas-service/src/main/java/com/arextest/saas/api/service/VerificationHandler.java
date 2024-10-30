package com.arextest.saas.api.service;

import com.arextest.saas.api.common.enums.ErrorCode;
import com.arextest.saas.api.common.exceptions.ArexSaasException;
import com.arextest.saas.api.common.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2024/8/9
 */
@Component
public class VerificationHandler {

  @Resource
  private ObjectMapper objectMapper;


  public VerificationEntity getVerificationInfoByToken(String token) {
    String upnString = JwtUtil.getUpnString(token);
    try {
      return objectMapper.readValue(upnString, VerificationEntity.class);
    } catch (JsonProcessingException e) {
      throw new ArexSaasException(ErrorCode.UPN_FORMAT_ERROR.getCodeValue(), "upn format error", e);
    }
  }

  public String generateToken(VerificationEntity verificationEntity) {
    try {
      return JwtUtil.makeAccessTokenWithUpn(objectMapper.writeValueAsString(verificationEntity));
    } catch (JsonProcessingException e) {
      throw new ArexSaasException(ErrorCode.UPN_FORMAT_ERROR.getCodeValue(), "upn format error", e);
    }
  }

  public String generateToken(String email, String verificationCode) {
    return generateToken(new VerificationEntity(email, verificationCode));
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class VerificationEntity {
    private String email;
    private String verificationCode;
  }
}
