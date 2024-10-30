package com.arextest.saas.api.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wildeslam.
 * @create 2024/3/21 14:57
 */
@Configuration
@EnableConfigurationProperties(IgnoreUrlsConfig.class)
public class InterceptorConfiguration implements WebMvcConfigurer {

  @Autowired
  private AuthorizationInterceptor authorizationInterceptor;

  @Autowired
  private IgnoreUrlsConfig ignoreUrlsConfig;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authorizationInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(ignoreUrlsConfig.getUrls());
  }
}
