package com.arextest.common.saas.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.saas.interceptor.TenantLimitService.TenantLimitInfo;
import com.arextest.common.saas.interceptor.TenantLimitService.TenantLimitResult;
import com.arextest.common.saas.utils.ResponseWriterUtil;
import com.arextest.common.saas.utils.TenantUtil;
import com.arextest.common.utils.TenantContextUtil;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

@Slf4j
@RequiredArgsConstructor
public class TenantInterceptor extends AbstractInterceptorHandler {

  private static final String HEALTH_CHECK_PATH = "/vi/health";
  private final TenantLimitService limitTenant;
  @Getter
  private final List<String> pathPatterns;

  @Getter
  private final List<String> excludePathPatterns;

  @Override
  public Integer getOrder() {
    return 0;
  }

  /**
   * 进行租户校验 对于/vi/health接口，没有tenantCode，进行系统状态校验。存在tenantCode，进行租户状态校验
   *
   * @param request
   * @param response
   * @param handler
   * @return
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    String tenantCode = TenantUtil.extractTenantCode(request);

    // verify system status
    String requestURI = request.getRequestURI();
    if (StringUtils.isEmpty(tenantCode) && Objects.equals(requestURI, HEALTH_CHECK_PATH)) {
      return false;
    }

    // reject the request if tenantCode is empty
    if (StringUtils.isEmpty(tenantCode)) {
      LOGGER.error("tenantCode is empty, reject the request, path:{}", request.getRequestURI());
      return false;
    }

    MDC.put("tenant", tenantCode);

    // verify tenant status
    TenantLimitInfo tenantLimitInfo = TenantLimitInfo.builder().tenantCode(tenantCode).build();
    TenantLimitResult tenantLimitResult = limitTenant.limitTenant(tenantLimitInfo);
    if (!tenantLimitResult.isPass()) {
      LOGGER.error("tenantCode:{}, errorCode:{}", tenantCode, tenantLimitResult.getErrorCode());
      ResponseWriterUtil.setDefaultErrorResponse(response, HttpStatus.UNAUTHORIZED,
          tenantLimitResult.getErrorCode());
      return false;
    }

    TenantContextUtil.setTenantCode(tenantCode);
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    TenantContextUtil.clear();
  }

}