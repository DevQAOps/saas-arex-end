package com.arextest.storage.saas.api.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import java.util.Comparator;
import java.util.List;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(value = "interceptorConfiguration")
public class SaasInterceptorConfiguration implements WebMvcConfigurer {

  @Resource
  List<AbstractInterceptorHandler> interceptors;


  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    if (CollectionUtils.isEmpty(interceptors)) {
      return;
    }
    interceptors.sort(Comparator.comparing(AbstractInterceptorHandler::getOrder));
    interceptors.forEach(interceptor -> {
      registry.addInterceptor(interceptor)
          .addPathPatterns(interceptor.getPathPatterns())
          .excludePathPatterns(interceptor.getExcludePathPatterns());
    });
  }
}
