package com.arextest.common.saas.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.saas.model.Constants;
import com.arextest.common.utils.TenantContextUtil;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

@Slf4j
public class TenantInterceptor extends AbstractInterceptorHandler {

  @Override
  public Integer getOrder() {
    return 0;
  }

  @Override
  public List<String> getPathPatterns() {
    return Collections.singletonList("/**");
  }

  @Override
  public List<String> getExcludePathPatterns() {
    return Collections.emptyList();
  }

  // todo: if group name is empty, we should reject the request
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    if (TenantContextUtil.getTenantCode() != null) {
      return true;
    }
    String tenantCode = extractTenantCode(request);
    TenantContextUtil.setTenantCode(tenantCode);
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    TenantContextUtil.clearAll();
  }

  private String extractTenantCode(HttpServletRequest request) {
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