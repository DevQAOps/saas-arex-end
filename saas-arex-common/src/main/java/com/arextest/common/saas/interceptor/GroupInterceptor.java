package com.arextest.common.saas.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.utils.GroupContextUtil;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class GroupInterceptor extends AbstractInterceptorHandler {

  private static final String ORG = "org";

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

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    String groupName = extractGroupName(request);
    GroupContextUtil.setGroup(groupName);
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    GroupContextUtil.clear();
  }

  private String extractGroupName(HttpServletRequest request) {
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
      res = request.getHeader(ORG) == null ? "" : request.getHeader(ORG);
    }
    return res;
  }

}