package com.arextest.common.saas.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.utils.GroupContextUtil;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GroupInterceptor extends AbstractInterceptorHandler {

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
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String serverName = request.getServerName();
    String groupName = extractGroupName(serverName);
    GroupContextUtil.setGroup(groupName);
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    GroupContextUtil.clear();
  }

  private String extractGroupName(String serverName) {
    Pattern pattern = Pattern.compile("^(.*?)\\.arextest\\.com$");
    Matcher matcher = pattern.matcher(serverName);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return "";
  }


}