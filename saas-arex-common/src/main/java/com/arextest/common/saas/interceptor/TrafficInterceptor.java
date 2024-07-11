package com.arextest.common.saas.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.saas.multitenant.usage.UsageCollector;
import com.arextest.common.utils.TenantContextUtil;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrafficInterceptor extends AbstractInterceptorHandler {

  @Override
  public Integer getOrder() {
    return Integer.MAX_VALUE;
  }

  @Override
  public List<String> getPathPatterns() {
    return Collections.singletonList("/**");
  }

  @Override
  public List<String> getExcludePathPatterns() {
    return Collections.emptyList();
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    try {
      String tenantCode = TenantContextUtil.getTenantCode();
      // no tenant traffic, skip
      if (StringUtils.isEmpty(tenantCode)) {
        return;
      }

      // 如果对应的请求 Controller 返回的是一个 Object，那么 Springboot 会使用 Transfer-Encoding: Chuncked
      // 这种情况下返回头中没有 Content-Length，暂时不统计
      UsageCollector.collect(request.getRequestURI(),
          request.getContentLengthLong(),
          Optional.ofNullable(response.getHeader("Content-Length"))
              .map(Long::getLong)
              .orElse(0L));
    } catch (Exception e) {
      LOGGER.error("Failed to collect usage", e);
    }
  }
}