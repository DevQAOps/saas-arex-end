package com.arextest.schedule.saas.api.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.jwt.JWTService;
import com.arextest.common.saas.interceptor.GroupInterceptor;
import com.arextest.common.saas.interceptor.SaasAuthorizationInterceptor;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterceptorHandlerAutoConfiguration {

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

  public List<String> getAuthorizationPathPatterns() {
    return Collections.singletonList("/**");
  }

  public List<String> getAuthorizationExcludePathPatterns() {
    return Lists.newArrayList("/vi/health");
  }
}
