package com.arextest.common.saas.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.saas.interceptor.TenantLimitService.TenantLimitInfo;
import com.arextest.common.saas.interceptor.TenantLimitService.TenantLimitResult;
import com.arextest.common.saas.model.Constants;
import com.arextest.common.saas.utils.ResponseWriterUtil;
import com.arextest.common.utils.TenantContextUtil;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;

@Slf4j
@RequiredArgsConstructor
public class TenantInterceptor extends AbstractInterceptorHandler {

  private final TenantLimitService limitTenant;

  @Override
  public Integer getOrder() {
    return 0;
  }

  @Getter
  private final List<String> pathPatterns;

  @Getter
  private final List<String> excludePathPatterns;

  private static final String HEALTH_CHECK_PATH = "/vi/health";

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
    String tenantCode = extractTenantCode(request);

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
    TenantContextUtil.clearAll();
  }

  private String extractTenantCode(HttpServletRequest request) {
    String tenantCode = TenantContextUtil.getTenantCode();
    if (StringUtils.isNotEmpty(tenantCode)) {
      return TenantContextUtil.getTenantCode();
    }
    return extractTenantCodeFromRequest(request);
  }


  private String extractTenantCodeFromRequest(HttpServletRequest request) {
    String res = "";
    String serverName = request.getServerName();
    if (StringUtils.isNotEmpty(serverName)) {
      Pattern pattern = Pattern.compile("^(.*?)\\.arextest\\.com$");
      Matcher matcher = pattern.matcher(serverName);
      if (matcher.find()) {
        res = matcher.group(1);
      }
    }

    if (StringUtils.isEmpty(res)) {
      String orgHeader = request.getHeader(Constants.AREX_TENANT_CODE);
      res = orgHeader == null ? Strings.EMPTY : orgHeader;
    }
    return res;
  }

}