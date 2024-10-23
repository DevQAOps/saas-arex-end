package com.arextest.common.saas.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.jwt.JWTService;
import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * for coustomize the pattern of interceptor
 */
@Slf4j
@NoArgsConstructor
public class SaasAuthorizationInterceptor extends AbstractInterceptorHandler {

  ObjectMapper mapper = new ObjectMapper();


  @Getter
  private List<String> pathPatterns;

  @Getter
  private List<String> excludePathPatterns;

  private JWTService jwtService;


  public SaasAuthorizationInterceptor(List<String> pathPatterns, List<String> excludePathPatterns,
      JWTService jwtService) {
    this.pathPatterns = pathPatterns;
    this.excludePathPatterns = excludePathPatterns;
    this.jwtService = jwtService;
  }

  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Object o)
      throws Exception {
    String authorization = httpServletRequest.getHeader("access-token");
    if (!jwtService.verifyToken(authorization)) {
      httpServletResponse.setStatus(200);
      httpServletResponse.setContentType("application/json");
      httpServletResponse.setCharacterEncoding("UTF-8");
      Response no_permission =
          ResponseUtils.errorResponse("Authentication verification failed",
              ResponseCode.AUTHENTICATION_FAILED);
      httpServletResponse.getWriter().write(mapper.writeValueAsString(no_permission));
      LOGGER.info(
          String.format("access-token invalid; path: %s", httpServletRequest.getServletPath()));
      return false;
    }
    return true;
  }

  @Override
  public Integer getOrder() {
    return 2;
  }
}