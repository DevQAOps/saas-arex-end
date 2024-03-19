package com.arextest.web.saas.api.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.saas.interceptor.GroupInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterceptorHandlerAutoConfiguration {

  @Bean
  public AbstractInterceptorHandler groupInterceptor (){
    return new GroupInterceptor();
  }

}
