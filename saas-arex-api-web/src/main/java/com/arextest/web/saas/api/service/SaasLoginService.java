package com.arextest.web.saas.api.service;

import com.arextest.common.exceptions.ArexException;
import com.arextest.common.jwt.JWTService;
import com.arextest.common.saas.enums.SaasErrorCode;
import com.arextest.web.core.repository.UserRepository;
import com.arextest.web.saas.model.contract.SaasVerifyRequestType;
import com.arextest.web.saas.model.contract.SaasVerifyResponseType;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class SaasLoginService {

  @Resource
  TicketTokenJwtService ticketTokenJwtService;

  @Resource
  private UserRepository userRepository;

  @Resource
  private JWTService jwtService;

  public SaasVerifyResponseType verify(SaasVerifyRequestType requestType) {
    String ticket = requestType.getTicket();
    String email = ticketTokenJwtService.getUserName(ticket);
    boolean existed = userRepository.existUserName(email);
    if (!existed) {
      throw new ArexException(SaasErrorCode.SAAS_USER_NOT_FOUND.getCodeValue(),
          SaasErrorCode.SAAS_USER_NOT_FOUND.getMessage());
    }
    String accessToken = jwtService.makeAccessToken(email);
    String refreshToken = jwtService.makeRefreshToken(email);
    SaasVerifyResponseType result = new SaasVerifyResponseType();
    result.setAccessToken(accessToken);
    result.setRefreshToken(refreshToken);
    return result;
  }


  @Component
  static class TicketTokenJwtService {

    private final static String TOKEN_SECRET_AREX_API_AUTH = "arex_secret_arex_api_auth";
    private final static String EMAIL = "email";

    public String getUserName(String ticket) {
      try {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET_AREX_API_AUTH);
        JWTVerifier build = JWT.require(algorithm).build();
        DecodedJWT verify = build.verify(ticket);
        return verify.getClaim(EMAIL).asString();
      } catch (Exception e) {
        return null;
      }
    }
  }

}
