package com.arextest.storage.saas.api.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.saas.tenant.TenantRedisHandler;
import com.arextest.common.saas.tenant.TenantStatusRedisInfo;
import com.arextest.common.saas.utils.ResponseWriterUtil;
import com.arextest.common.utils.TenantContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

  private final TenantRedisHandler tenantRedisHandler;

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

    AgentTokenContext agentTokenContext = new AgentTokenContext();
    agentTokenContext.setAgentToken(apiToken);

    return parseApiToken(agentTokenContext)
        .flatMap(this::extractTenantCode)
        .flatMap(this::verifyToken)
        .map(item -> {
          TenantContextUtil.setTenantCode(item.getTenantCode());
          return true;
        })
        .orElseGet(() -> {
          ResponseWriterUtil.setDefaultErrorResponse(response, HttpStatus.UNAUTHORIZED, null);
          return false;
        });
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    TenantContextUtil.clearAll();
  }

  /**
   * parse api token
   *
   * @param agentTokenContext
   * @return
   */
  private Optional<AgentTokenContext> parseApiToken(AgentTokenContext agentTokenContext) {
    String apiToken = agentTokenContext.getAgentToken();
    if (StringUtils.isEmpty(apiToken)) {
      return Optional.empty();
    }
    String[] split = apiToken.split(SPLIT_SYMBOL);
    if (split.length != 2) {
      return Optional.empty();
    }
    agentTokenContext.setEncryptedToken(split[1]);
    return Optional.of(agentTokenContext);
  }


  /**
   * apiToken: arex-api-token:
   * ctrip:S13V3lnts9q9gnLwDp8w7M32CQtlwkVyUaHs2P+G9lCVpgbcmEsyFUKOlQf3OWfTF1uuS03LaGv40wlgRLCH/Q==
   *
   * @param agentTokenContext
   * @return
   */
  private Optional<AgentTokenContext> extractTenantCode(AgentTokenContext agentTokenContext) {
    String encryptedToken = agentTokenContext.getEncryptedToken();
    if (StringUtils.isEmpty(encryptedToken)) {
      return Optional.empty();
    }

    TenantTokenInfo tenantTokenInfo;
    try {
      String tenantTokenJson = decrypt(encryptedToken);
      tenantTokenInfo = objectMapper.readValue(tenantTokenJson, TenantTokenInfo.class);
    } catch (Exception e) {
      LOGGER.error("decrypt token error, exception:{}", e.getMessage());
      return Optional.empty();
    }

    if (tenantTokenInfo == null || StringUtils.isEmpty(tenantTokenInfo.getTenantCode())) {
      return Optional.empty();
    }
    agentTokenContext.setTenantCode(tenantTokenInfo.getTenantCode());
    return Optional.of(agentTokenContext);
  }


  /**
   * verify the exists of token,
   *
   * @param agentTokenContext
   * @return
   */
  private Optional<AgentTokenContext> verifyToken(AgentTokenContext agentTokenContext) {
    TenantStatusRedisInfo tenantStatus = tenantRedisHandler.getTenantStatus(
        agentTokenContext.getTenantCode());
    return Objects.equals(tenantStatus.getTenantToken(), agentTokenContext.getAgentToken())
        ? Optional.of(agentTokenContext)
        : Optional.empty();
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
  private static class AgentTokenContext {

    // ctrip:S13V3lnts9q9gnLwDp8w7M32CQtlwkVyUaHs2P+G9lAst4LRtssybxxWFaKoAkAuF1uuS03LaGv40wlgRLCH/Q==
    private String agentToken;
    // S13V3lnts9q9gnLwDp8w7M32CQtlwkVyUaHs2P+G9lAst4LRtssybxxWFaKoAkAuF1uuS03LaGv40wlgRLCH/Q==
    private String encryptedToken;
    // ctrip
    private String tenantCode;
  }

  @Data
  private static class TenantTokenInfo {

    private String tenantCode;
  }

}
