package com.arextest.common.saas.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.jwt.JWTService;
import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by rchen9 on 2022/8/4.
 */
@Slf4j
public class SaasRefreshInterceptor extends AbstractInterceptorHandler {

  ObjectMapper mapper = new ObjectMapper();

  private JWTService jwtService;

  @Getter
  private List<String> pathPatterns;

  @Getter
  private List<String> excludePathPatterns;

  public SaasRefreshInterceptor(List<String> pathPatterns, List<String> excludePathPatterns,
      JWTService jwtService) {
    this.pathPatterns = pathPatterns;
    this.excludePathPatterns = excludePathPatterns;
    this.jwtService = jwtService;
  }

  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Object o)
      throws Exception {
    String authorization = httpServletRequest.getHeader("refresh-token");
    if (!jwtService.verifyToken(authorization)) {
      httpServletResponse.setStatus(200);
      httpServletResponse.setContentType("application/json");
      httpServletResponse.setCharacterEncoding("UTF-8");
      Response no_permission =
          ResponseUtils.errorResponse("Authentication verification failed",
              ResponseCode.AUTHENTICATION_FAILED);
      httpServletResponse.getWriter().write(mapper.writeValueAsString(no_permission));
      LOGGER.info(
          String.format("refresh-token invalid; path: %s", httpServletRequest.getServletPath()));
      return false;
    }
    return true;
  }

  @Override
  public Integer getOrder() {
    return 1;
  }
}