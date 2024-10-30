package com.arextest.saas.api.service;


import com.arextest.saas.api.repo.TenantRepository;
import com.arextest.saas.api.common.enums.ErrorCode;
import com.arextest.saas.api.common.exceptions.ArexSaasException;
import com.arextest.saas.api.common.utils.JwtUtil;
import com.arextest.saas.api.model.contract.OauthLoginRequest;
import com.arextest.saas.api.model.contract.OauthLoginResponse;
import com.arextest.saas.api.model.dto.TenantDto;
import com.arextest.saas.api.model.dto.login.OauthInfoDto;
import com.arextest.saas.api.model.dto.login.OauthResult;
import com.arextest.saas.api.model.enums.TenantLevelEnum;
import com.arextest.saas.api.model.mapper.login.OauthInfoDtoMapper;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wildeslam.
 * @create 2024/3/6 20:42
 */
@Component
public class OauthHandler {

  @Autowired
  List<OauthService> oauthServices;

  @Autowired
  TenantRepository tenantRepository;

  public OauthLoginResponse oauthLogin(OauthLoginRequest request) {
    OauthLoginResponse response = new OauthLoginResponse();

    OauthService oauthService = oauthServices.stream()
        .filter(item -> Objects.equals(item.getOauthType().getCode(), request.getOauthType()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("oauthType not found"));

    OauthInfoDto oauthInfoDto = OauthInfoDtoMapper.INSTANCE.dtoFromContract(request);
    OauthResult oauthResult;
    try {
      oauthResult = oauthService.oauth(oauthInfoDto);
    } catch (Exception e) {
      throw new ArexSaasException(ErrorCode.OAUTH_RESULT_GET_EXCEPTION.getCodeValue(),
          e.getMessage());
    }
    if (oauthResult == null) {
      throw new ArexSaasException(ErrorCode.OAUTH_RESULT_GET_EXCEPTION.getCodeValue(),
          "failed to get oauth result from oauth service");
    }

    String email = oauthResult.getEmail();
    response.setEmail(email);

    TenantDto tenantDto = tenantRepository.queryTenantByEmail(email);
    if (tenantDto != null && tenantDto.isEnabled()) {
      Set<String> providerUids = Optional.ofNullable(tenantDto.getProviderUids())
          .orElse(new HashSet<>());
      if (oauthResult.getProviderUid() != null
          && !CollectionUtils.containsAny(providerUids, oauthResult.getProviderUid())) {
        providerUids.add(oauthResult.getProviderUid());
        tenantDto.setProviderUids(providerUids);
        tenantRepository.upsertTenant(tenantDto);
      }
      response.setAccessToken(JwtUtil.makeAccessTokenWithTenantCode(tenantDto.getTenantCode()));
      response.setNeedBind(false);
    } else {
      response.setNeedBind(true);
      response.setAccessToken(JwtUtil.makeAccessTokenWithEmail(email));
      tenantDto = new TenantDto();
      tenantDto.setEmail(email);
      tenantDto.setUserLevel(TenantLevelEnum.DISABLED.getCode());
      if (oauthResult.getProviderUid() != null) {
        tenantDto.setProviderUids(Collections.singleton(oauthResult.getProviderUid()));
      }
      tenantRepository.upsertTenant(tenantDto);
    }
    return response;
  }
}
