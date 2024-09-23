package com.arextest.common.saas.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SaasServiceJWTService {

  private final String tokenSecret;

  public String makeAccessToken(String tenantCode, long accessExpireTimeMillis) {
    Date date = new Date(System.currentTimeMillis() + accessExpireTimeMillis);
    Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
    return JWT.create()
        .withExpiresAt(date)
        .withClaim("tenantCode", tenantCode)
        .sign(algorithm);
  }



}
