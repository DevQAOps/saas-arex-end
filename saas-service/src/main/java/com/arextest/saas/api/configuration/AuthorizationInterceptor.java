package com.arextest.saas.api.configuration;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.saas.api.common.utils.JwtUtil;
import com.arextest.web.common.LogUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author wildeslam.
 * @create 2024/3/21 14:50
 */
@Slf4j
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

  private static final Set<String> EMAIL_TOKEN_URLS = Set.of(
      "/api/login/bind",
      "/api/login/resetPassword");
  ObjectMapper mapper = new ObjectMapper();

  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Object o)
      throws Exception {
    String authorization = httpServletRequest.getHeader("access-token");
    String url = httpServletRequest.getRequestURI();
    if (!validateToken(url, authorization)) {
      httpServletResponse.setStatus(200);
      httpServletResponse.setContentType("application/json");
      httpServletResponse.setCharacterEncoding("UTF-8");
      Response no_permission =
          ResponseUtils.errorResponse("Authentication verification failed",
              ResponseCode.AUTHENTICATION_FAILED);
      httpServletResponse.getWriter().write(mapper.writeValueAsString(no_permission));
      LogUtils.info(LOGGER,
          String.format("access-token invalid; path: %s", httpServletRequest.getServletPath()));
      return false;
    }
    return true;
  }

  private boolean validateToken(String url, String token) {
    if (EMAIL_TOKEN_URLS.contains(url)) {
      return JwtUtil.verifyTokenWithEmail(token);
    } else {
      return JwtUtil.verifyTokenWithUser(token);
    }
  }
}
