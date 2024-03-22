package com.arextest.storage.saas.api.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.jwt.JWTService;
import com.arextest.common.saas.interceptor.GroupInterceptor;
import com.arextest.common.saas.interceptor.SaasAuthorizationInterceptor;
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
//    return Collections.singletonList("/**");
    return Collections.emptyList();
  }

  public List<String> getAuthorizationExcludePathPatterns() {
//    List<String> defaultPatterns = new ArrayList<>(5);
//    // for agent
//    defaultPatterns.add("/api/config/agent/*");
//    defaultPatterns.add("/api/storage/record/*");
//    // for inner call
//
//    return defaultPatterns;
    return Collections.singletonList("/**");
  }
}
