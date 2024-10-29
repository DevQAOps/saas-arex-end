package com.arextest.saas.api.service.impl.clientlogin;

import com.arextest.saas.api.service.ClientOauthService;
import com.arextest.saas.api.service.http.HttpProxyFactory;
import com.arextest.saas.api.common.enums.ErrorCode;
import com.arextest.saas.api.common.exceptions.ArexSaasException;
import com.arextest.saas.api.model.dto.ClientOauthInfoDto;
import com.arextest.saas.api.model.dto.ClientOauthResultDto;
import com.arextest.saas.api.model.enums.ClientOauthTypeEnum;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Oauth0ClientOauthServiceImpl implements ClientOauthService {

  private static final String EMAIL = "email";
  private static final String EMAIL_VERIFIED = "email_verified";

  private JWTVerifier jwtVerifier = null;

  @Value("${arex.oauth.oauth0.auth0IssuerBaseUrl}")
  private String auth0IssuerBaseUrl;

  @Resource
  private HttpProxyFactory httpProxyFactory;

  @PostConstruct
  public void init() {
    jwtVerifier = initJwtVerifier();
  }


  @Override
  public ClientOauthTypeEnum supportOauthType() {
    return ClientOauthTypeEnum.OAUTH0;
  }

  @Override
  public ClientOauthResultDto doOauth(ClientOauthInfoDto clientOauthInfoDto) {
    if (jwtVerifier == null) {
      throw new ArexSaasException(ErrorCode.CLIENT_LOGIN_AUTH0_VERIFY_FAILED.getCodeValue(),
          "jwtVerifier is null");
    }

    String idToken = clientOauthInfoDto.getCode();
    DecodedJWT decodedJWT = null;
    try {
      decodedJWT = jwtVerifier.verify(idToken);
    } catch (Exception e) {
      LOGGER.error("auth0 verify failed", e);
      throw new ArexSaasException(ErrorCode.CLIENT_LOGIN_PROVIDER_NOT_SUPPORTED.getCodeValue(), e);
    }
    Map<String, String> providerProfile = new HashMap<>();
    Map<String, Claim> claims = decodedJWT.getClaims();
    if (MapUtils.isNotEmpty(claims)) {
      for (Map.Entry<String, Claim> entry : claims.entrySet()) {
        providerProfile.put(entry.getKey(), entry.getValue().as(String.class));
      }
    }

    ClientOauthResultDto result = new ClientOauthResultDto();
    result.setEmail(providerProfile.get(EMAIL));
    result.setEmailVerified(Objects.equals(providerProfile.get(EMAIL_VERIFIED), "true"));
    result.setProviderProfile(providerProfile);
    return result;
  }

  private JWTVerifier initJwtVerifier() {

    RSAKeyProvider keyProvider = new RSAKeyProvider() {

      JwkProvider provider = new JwkProviderBuilder(auth0IssuerBaseUrl)
          .cached(10, 24, TimeUnit.HOURS)
          .rateLimited(10, 1, TimeUnit.MINUTES)
          .proxied(httpProxyFactory.createProxy())
          .timeouts(5000, 10000)
          .build();

      @Override
      public RSAPublicKey getPublicKeyById(String kid) {
        try {
          return (RSAPublicKey) provider.get(kid).getPublicKey();
        } catch (JwkException e) {
          LOGGER.error("autho0 getPublicKeyById failed" + e);
        }
        return null;
      }

      @Override
      public RSAPrivateKey getPrivateKey() {
        // return the private key used
        return null;
      }

      @Override
      public String getPrivateKeyId() {
        return null;
      }
    };

    Algorithm algorithm = Algorithm.RSA256(keyProvider);
    try {
      return JWT.require(algorithm).build();
    } catch (Exception var4) {
      LOGGER.error("auth0 build failed", var4);
    }
    return null;
  }
}
