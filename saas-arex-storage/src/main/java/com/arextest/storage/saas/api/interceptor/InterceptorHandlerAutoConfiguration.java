package com.arextest.storage.saas.api.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.jwt.JWTService;
import com.arextest.common.saas.interceptor.SaasAuthorizationInterceptor;
import com.arextest.common.saas.interceptor.TenantInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterceptorHandlerAutoConfiguration {

  @Bean
  public AbstractInterceptorHandler tenantInterceptor() {
    return new TenantInterceptor();
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
  public AbstractInterceptorHandler agentAccessInterceptorHandler(
      @Value("${arex.agent.aesKey:}") String aesKey, ObjectMapper objectMapper) {
    return new AgentAccessInterceptorHandler(aesKey, objectMapper);
  }

  public List<String> getAuthorizationPathPatterns() {
    return Collections.singletonList("/**");
  }

  public List<String> getAuthorizationExcludePathPatterns() {
    List<String> defaultPatterns = new ArrayList<>(5);
    // for agent
    defaultPatterns.add("/api/config/agent/**");
    defaultPatterns.add("/api/storage/record/**");
    defaultPatterns.add("/vi/health");
    return defaultPatterns;
  }
}
