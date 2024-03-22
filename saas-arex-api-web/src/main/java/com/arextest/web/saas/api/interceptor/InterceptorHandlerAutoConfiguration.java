package com.arextest.web.saas.api.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.jwt.JWTService;
import com.arextest.common.saas.interceptor.GroupInterceptor;
import com.arextest.common.saas.interceptor.SaasAuthorizationInterceptor;
import com.arextest.common.saas.interceptor.SaasRefreshInterceptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterceptorHandlerAutoConfiguration {

  @Value("${arex.interceptor.patterns}")
  private String interceptorPatterns;

  @Bean
  public AbstractInterceptorHandler groupInterceptor() {
    return new GroupInterceptor();
  }

  @Bean
  public AbstractInterceptorHandler authorizationInterceptor(JWTService jwtService) {
    return new SaasAuthorizationInterceptor(
        getAuthorizationPathPatterns(),
        getAuthorizationExcludePathPatterns(),
        jwtService
    );
  }

  @Bean
  public AbstractInterceptorHandler refreshInterceptor(JWTService jwtService) {
    return new SaasRefreshInterceptor(
        getRefreshPathPatterns(),
        getRefreshExcludePathPatterns(),
        jwtService
    );
  }


  public List<String> getAuthorizationPathPatterns() {
    return Collections.singletonList("/**");
  }

  public List<String> getAuthorizationExcludePathPatterns() {
    List<String> defaultPatterns = new ArrayList<>(20);
    // error
    defaultPatterns.add("/error");
    // static resource
    defaultPatterns.add("/js/**");
    defaultPatterns.add("/css/**");
    defaultPatterns.add("/images/**");
    defaultPatterns.add("/lib/**");
    defaultPatterns.add("/fonts/**");
    // swagger-ui
    defaultPatterns.add("/swagger-resources/**");
    defaultPatterns.add("/webjars/**");
    defaultPatterns.add("/v3/**");
    defaultPatterns.add("/swagger-ui/**");
    defaultPatterns.add("/api/login/verify");
    defaultPatterns.add("/api/login/getVerificationCode/**");
    defaultPatterns.add("/api/login/loginAsGuest");
    defaultPatterns.add("/api/login/oauthLogin");
    defaultPatterns.add("/api/login/oauthInfo/**");
    defaultPatterns.add("/api/login/refresh/**");
    // healthCheck
    defaultPatterns.add("/vi/health");
    // called by arex-schedule
    defaultPatterns.add("/api/report/init");
    defaultPatterns.add("/api/report/pushCompareResults");
    defaultPatterns.add("/api/report/pushReplayStatus");
    defaultPatterns.add("/api/report/updateReportInfo");
    defaultPatterns.add("/api/report/analyzeCompareResults");
    defaultPatterns.add("/api/report/removeRecordsAndScenes");
    defaultPatterns.add("/api/report/removeErrorMsg");
    defaultPatterns.add("/api/system/config/list");
    defaultPatterns.add("/api/config/comparison/summary/queryConfigOfCategory");
    defaultPatterns.add("/api/report/queryPlanStatistic");
    defaultPatterns.add("/api/desensitization/listJar");

    // exclude configuration services
    defaultPatterns.add("/api/config/**");
    defaultPatterns.add("/api/report/listCategoryType");
    // exclude logs services
    defaultPatterns.add("/api/logs/**");
    // invite to workspace
    defaultPatterns.add("/api/filesystem/validInvitation");

    // add custom patterns
    if (StringUtils.isNotBlank(interceptorPatterns)) {
      String[] patterns = interceptorPatterns.split(",");
      defaultPatterns.addAll(Arrays.asList(patterns));
    }
    return defaultPatterns;
  }

  public List<String> getRefreshPathPatterns() {
    return Collections.singletonList("/api/login/refreshToken");
  }

  public List<String> getRefreshExcludePathPatterns() {
    return Collections.emptyList();
  }

}
