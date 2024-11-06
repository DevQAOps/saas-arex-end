package com.arextest.saas.api.service.impl;

import com.arextest.saas.api.common.enums.ErrorCode;
import com.arextest.saas.api.common.exceptions.ArexSaasException;
import com.arextest.saas.api.service.ClientOauthService;
import com.arextest.saas.api.service.ClientService;
import com.arextest.saas.api.service.jwt.ArexApiSystemAuthJwtService;
import com.arextest.saas.api.model.contract.ClientDownloadResponse;
import com.arextest.saas.api.model.contract.ClientDownloadResponse.InnerClass;
import com.arextest.saas.api.model.contract.ClientOauthRequest;
import com.arextest.saas.api.model.contract.ClientOauthResponse;
import com.arextest.saas.api.model.dto.ClientOauthInfoDto;
import com.arextest.saas.api.model.dto.ClientOauthResultDto;
import com.arextest.saas.api.model.mapper.ClientOauthInfoMapper;
import com.arextest.saas.api.model.vo.TenantVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @author wildeslam.
 * @create 2024/5/20 19:48
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(AdminConfig.class)
public class ClientServiceImpl implements ClientService {
  private static final String QUERY_RELEASES_URL = "https://api.github.com/repos/arextest/releases/releases/latest";
  private static final String AUTHORIZATION = "Authorization";
  private static final String AUTH_TOKEN = "Bearer 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";
  private static final String API_VERSION_HEADER = "X-GitHub-Api-Version";
  private static final String API_VERSION = "2022-11-28";
  private static final String EXE = ".exe";
  private static final String ARM = "arm64";
  private static final String X64 = "x64";
  private static final String DMG = ".dmg";
  private static final String CACHE_KEY = "latest_client_url";
  private static final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private final Cache<String, Object> cache = CacheBuilder.newBuilder()
      .maximumSize(100)
      .expireAfterWrite(5, TimeUnit.MINUTES)
      .build();


  private final List<ClientOauthService> clientOauthServices;
  private final TenantServiceImpl tenantService;
  private final ArexApiSystemAuthJwtService arexApiSystemAuthJwtService;
  private final AdminConfig adminConfig;

  @Override
  public ClientDownloadResponse getBrowserDownloadUrl() {
    ClientDownloadResponse clientDownloadResponse;
    try {
      clientDownloadResponse = (ClientDownloadResponse) cache.get(CACHE_KEY,
          this::buildClientDownloadResponse);
    } catch (Exception e) {
      LOGGER.error("Failed to get latest client version", e);
      clientDownloadResponse = new ClientDownloadResponse();
      clientDownloadResponse.setSuccess(false);
    }
    return clientDownloadResponse;
  }

  @Override
  public ClientOauthResponse clientLogin(ClientOauthRequest clientOauthRequest) {
    ClientOauthService clientOauthService = getClientOauthService(
        clientOauthRequest.getOauthType());
    ClientOauthInfoDto clientOauthInfoDto = ClientOauthInfoMapper.INSTANCE.contractToDto(
        clientOauthRequest);
    ClientOauthResultDto clientOauthResultDto = clientOauthService.doOauth(clientOauthInfoDto);

    // verify whether the email is empty
    String email = clientOauthResultDto.getEmail();
    if (StringUtils.isEmpty(email)) {
      throw new ArexSaasException(ErrorCode.CLIENT_LOGIN_PROVIDER_NOT_SUPPORTED.getCodeValue(),
          "Failed to get email from oauth provider");
    }

    // verify whether the email has been verified
    if (!clientOauthResultDto.isEmailVerified()) {
      throw new ArexSaasException(ErrorCode.CLIENT_LOGIN_EMAIL_NOT_VERIFIED.getCodeValue(),
          "Email is not verified");
    }

    // verify whether the email has been in the tenant list
    List<TenantVo> tenantInfos = adminConfig.getAdmins().contains(email) ? tenantService.listAllTenants() :
        tenantService.queryTenantsByEmail(email);

    if (CollectionUtils.isEmpty(tenantInfos)) {
      throw new ArexSaasException(ErrorCode.CLIENT_LOGIN_USER_NOT_FOUND.getCodeValue(),
          "No tenant found for email");
    }

    String authToken = arexApiSystemAuthJwtService.makeArexApiAuthToken(email);
    ClientOauthResponse clientOauthResponse = ClientOauthInfoMapper.INSTANCE.dtoToResContract(
        clientOauthResultDto);
    clientOauthResponse.setTenantInfos(tenantInfos);
    clientOauthResponse.setAuthToken(authToken);
    return clientOauthResponse;
  }


  private ClientDownloadResponse buildClientDownloadResponse() {
    ClientDownloadResponse clientDownloadResponse = new ClientDownloadResponse();

    ReleasesResponse releasesResponse = getReleasesResponse();
    if (releasesResponse != null && CollectionUtils.isNotEmpty(releasesResponse.getAssets())) {
      for (AssetInfo assetInfo : releasesResponse.getAssets()) {
        String browserDownloadUrl = assetInfo.getBrowserDownloadUrl();
        if (browserDownloadUrl.endsWith(EXE)) {
          // 往response里的windows里写，如果windows为null先new一个
          InnerClass innerClass = clientDownloadResponse.getWindows();
          if (innerClass == null) {
            innerClass = new InnerClass();
          }
          if (browserDownloadUrl.contains(ARM)) {
            innerClass.setArmDownloadUrl(browserDownloadUrl);
          } else if (browserDownloadUrl.contains(X64)) {
            innerClass.setX64DownloadUrl(browserDownloadUrl);
          } else {
            // default to x86
            innerClass.setX64DownloadUrl(browserDownloadUrl);
          }
          clientDownloadResponse.setWindows(innerClass);
        } else if (browserDownloadUrl.endsWith(DMG)) {
          InnerClass innerClass = clientDownloadResponse.getMac();
          if (innerClass == null) {
            innerClass = new InnerClass();
          }
          if (browserDownloadUrl.contains(ARM)) {
            innerClass.setArmDownloadUrl(browserDownloadUrl);
          } else if (browserDownloadUrl.contains(X64)) {
            innerClass.setX64DownloadUrl(browserDownloadUrl);
          }
          clientDownloadResponse.setMac(innerClass);
        }
      }
    } else {
      return null;
    }
    clientDownloadResponse.setSuccess(true);
    return clientDownloadResponse;
  }

  private ReleasesResponse getReleasesResponse() {
    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(QUERY_RELEASES_URL);
    request.setHeader(AUTHORIZATION, AUTH_TOKEN);
    request.setHeader(API_VERSION_HEADER, API_VERSION);
    try {
      HttpResponse response = httpClient.execute(request);
      String jsonString = EntityUtils.toString(response.getEntity());
      return objectMapper.readValue(jsonString, ReleasesResponse.class);
    } catch (Exception e) {
      LOGGER.error("Failed to get latest client version", e);
      return null;
    }
  }


  private ClientOauthService getClientOauthService(Integer code) {
    return clientOauthServices.stream().filter(
            clientOauthService -> Objects.equals(
                clientOauthService.supportOauthType().getCode(), code)
        )
        .findFirst()
        .orElseThrow(() -> new ArexSaasException(
            ErrorCode.CLIENT_LOGIN_PROVIDER_NOT_SUPPORTED.getCodeValue(),
            "Unsupported oauth type")
        );
  }


  @Data
  static class ReleasesResponse {

    private List<AssetInfo> assets;
  }

  @Data
  static class AssetInfo {

    @JsonProperty("browser_download_url")
    private String browserDownloadUrl;
  }
}
