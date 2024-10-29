package com.arextest.saas.api.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ArexApiSystemAuthJwtService {

  @Value("${arex.client.login.tokenExpirationMS}")
  private long clientLoginTokenExpireTimeMs;

  @Value("${arex.client.login.tokenSecret}")
  private String clientLoginSecret;


  public String makeArexApiAuthToken(String email) {
    Date date = new Date(System.currentTimeMillis() + clientLoginTokenExpireTimeMs);
    Algorithm algorithm = Algorithm.HMAC256(clientLoginSecret);
    return JWT.create()
        .withExpiresAt(date)
        .withClaim("email", email)
        .sign(algorithm);
  }


}
