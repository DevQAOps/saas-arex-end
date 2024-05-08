package com.arextest.storage.saas.api.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.utils.TenantContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;


@Slf4j
@RequiredArgsConstructor
public class AgentAccessInterceptorHandler extends AbstractInterceptorHandler {

  private final String aesKey;

  private final ObjectMapper objectMapper;

  private static final String SPLIT_SYMBOL = ":";

  @Override
  public Integer getOrder() {
    return -1;
  }

  @Override
  public List<String> getPathPatterns() {
    return Arrays.asList("/api/config/agent/**", "/api/storage/record/**");
  }

  @Override
  public List<String> getExcludePathPatterns() {
    return Collections.emptyList();
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) throws IOException {
    String apiToken = request.getHeader("arex-api-token");
    String tenantCode = extractTenantCode(apiToken);
    if (StringUtils.isEmpty(tenantCode)) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      return false;
    }
    TenantContextUtil.setTenantCode(tenantCode);
    return true;
  }

  /**
   * apiToken: arex-api-token:
   * ctrip:S13V3lnts9q9gnLwDp8w7M32CQtlwkVyUaHs2P+G9lCVpgbcmEsyFUKOlQf3OWfTF1uuS03LaGv40wlgRLCH/Q==
   *
   * @param apiToken
   * @return
   */
  private String extractTenantCode(String apiToken) {
    if (StringUtils.isEmpty(apiToken)) {
      return null;
    }
    CompanyToken companyToken;
    try {
      String[] split = apiToken.split(SPLIT_SYMBOL);
      if (split.length != 2) {
        return null;
      }
      String companyTokenJson = decrypt(split[1]);
      companyToken = objectMapper.readValue(companyTokenJson, CompanyToken.class);
    } catch (Exception e) {
      LOGGER.error("decrypt token error, exception:{}", e.getMessage());
      return null;
    }

    if (companyToken == null || StringUtils.isEmpty(companyToken.getTenantCode())) {
      return null;
    }

    return companyToken.getTenantCode();
  }

  public String decrypt(String s) throws Exception {
    byte[] base64DecodeContent = Base64.getDecoder().decode(s);
    byte[] aesKeyBytes = Base64.getDecoder().decode(aesKey);
    byte[] decryptedContent = decryptAES(base64DecodeContent, aesKeyBytes);
    return new String(decryptedContent);
  }

  /**
   * decrypt
   */
  private byte[] decryptAES(byte[] data, byte[] key) throws Exception {
    SecretKey secretKey = new SecretKeySpec(key, "AES");
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    return cipher.doFinal(data);
  }


  @Data
  private static class CompanyToken {

    private String tenantCode;
  }

}
