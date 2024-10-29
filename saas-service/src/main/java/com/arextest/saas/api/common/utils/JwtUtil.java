package com.arextest.saas.api.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wildeslam.
 * @create 2024/3/21 11:24
 */
public class JwtUtil {

  private final static long ACCESS_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
  private final static long REFRESH_EXPIRE_TIME = 30 * 24 * 60 * 60 * 1000L;
  private static final String TOKEN_SECRET_EMAIL = "arex_secret_email";
  private static final String TOKEN_SECRET_USER = "arex_secret_user";
  private static final String TOKEN_SECRET_UPN = "areX_secret_upn";
  private static final String CLAIM_NAME = "username";

  public static String makeAccessTokenWithEmail(String username) {
    return makeAccessToken(TOKEN_SECRET_EMAIL, username);
  }

  public static String makeAccessTokenWithTenantCode(String tenantCode) {
    return makeAccessToken(TOKEN_SECRET_USER, tenantCode);
  }

  public static String makeRefreshTokenWithEmail(String username) {
    return refreshAccessToken(TOKEN_SECRET_EMAIL, username);
  }

  public static String makeRefreshTokenWithUser(String username) {
    return refreshAccessToken(TOKEN_SECRET_USER, username);
  }

  public static boolean verifyTokenWithEmail(String field) {
    return verifyToken(TOKEN_SECRET_EMAIL, field);
  }

  public static boolean verifyTokenWithUser(String field) {
    return verifyToken(TOKEN_SECRET_USER, field);
  }

  public static String getUserNameByUserToken(String token) {
    return getUserName(token, TOKEN_SECRET_USER);
  }

  public static String getUserNameByEmailToken(String token) {
    return getUserName(token, TOKEN_SECRET_EMAIL);
  }


  private static String getUserName(String token, String secret) {
    if (StringUtils.isEmpty(token)) {
      return null;
    } else {
      try {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier build = JWT.require(algorithm).build();
        DecodedJWT verify = build.verify(token);
        return verify.getClaim(CLAIM_NAME).asString();
      } catch (Exception e) {
        return null;
      }
    }
  }

  private static String makeAccessToken(String secrete, String claimValue) {
    Date date = new Date(System.currentTimeMillis() + ACCESS_EXPIRE_TIME);
    Algorithm algorithm = Algorithm.HMAC256(secrete);
    return JWT.create()
        .withExpiresAt(date)
        .withClaim(CLAIM_NAME, claimValue)
        .sign(algorithm);
  }

  private static String refreshAccessToken(String secrete, String claimValue) {
    Date date = new Date(System.currentTimeMillis() + REFRESH_EXPIRE_TIME);
    Algorithm algorithm = Algorithm.HMAC256(secrete);
    return JWT.create()
        .withExpiresAt(date)
        .withClaim(CLAIM_NAME, claimValue)
        .sign(algorithm);
  }

  private static boolean verifyToken(String secrete, String field) {
    if (StringUtils.isEmpty(field)) {
      return false;
    }
    return getToken(secrete, field) != null;
  }

  private static DecodedJWT getToken(String secret, String token) {
    if (StringUtils.isEmpty(token)) {
      return null;
    }
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      JWTVerifier build = JWT.require(algorithm).build();
      return build.verify(token);
    } catch (Exception e) {
      return null;
    }
  }

  public static String makeAccessTokenWithUpn(String upnString) {
    Date date = new Date(System.currentTimeMillis() + ACCESS_EXPIRE_TIME);
    Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET_UPN);
    return JWT.create()
        .withExpiresAt(date)
        .withClaim("upn", upnString)
        .sign(algorithm);
  }

  public static String getUpnString(String token) {
    if (StringUtils.isEmpty(token)) {
      return null;
    } else {
      try {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET_UPN);
        JWTVerifier build = JWT.require(algorithm).build();
        DecodedJWT verify = build.verify(token);
        return verify.getClaim("upn").asString();
      } catch (Exception e) {
        return null;
      }
    }
  }
}
