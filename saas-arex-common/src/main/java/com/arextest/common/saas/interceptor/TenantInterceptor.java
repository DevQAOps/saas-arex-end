package com.arextest.common.saas.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.saas.enums.SaasErrorCode;
import com.arextest.common.saas.interceptor.TenantLimitService.TenantLimitInfo;
import com.arextest.common.saas.interceptor.TenantLimitService.TenantLimitResult;
import com.arextest.common.saas.model.Constants;
import com.arextest.common.saas.utils.ResponseWriterUtil;
import com.arextest.common.utils.TenantContextUtil;
import java.util.List;
import java.util.Objects;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
   * Perform tenant verification For /vi/health interface, If there is no tenantCode, perform system
   * status verification. If there is tenantCode, perform tenant status verification
   *
   * @param request
   * @param response
   * @param handler
   * @return
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    String tenantCode = extractTenantCodeFromRequest(request);

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

    try {
      TenantContextUtil.setTenantCode(tenantCode);
      // verify tenant status
      TenantLimitInfo tenantLimitInfo = TenantLimitInfo.builder().tenantCode(tenantCode).build();
      TenantLimitResult tenantLimitResult = limitTenant.limitTenant(tenantLimitInfo);
      if (!tenantLimitResult.isPass()) {
        TenantContextUtil.clear();
        LOGGER.error("tenantCode:{}, errorCode:{}", tenantCode, tenantLimitResult.getErrorCode());
        ResponseWriterUtil.setDefaultErrorResponse(response, HttpStatus.UNAUTHORIZED,
            tenantLimitResult.getErrorCode());
        return false;
      }
      return true;
    } catch (Exception e) {
      TenantContextUtil.clear();
      LOGGER.error("tenantCode:{}, error:{}", tenantCode, e);
      ResponseWriterUtil.setDefaultErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR,
          SaasErrorCode.SAAS_COMMON_ERROR);
      return false;
    }
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    TenantContextUtil.clear();
  }


  private static String extractTenantCodeFromRequest(HttpServletRequest request) {
    String tenantCode = TenantContextUtil.getTenantCode();
    if (StringUtils.isNotEmpty(tenantCode)) {
      return TenantContextUtil.getTenantCode();
    }
    String orgHeader = request.getHeader(Constants.AREX_TENANT_CODE);
    return orgHeader == null ? StringUtils.EMPTY : orgHeader;
  }

}