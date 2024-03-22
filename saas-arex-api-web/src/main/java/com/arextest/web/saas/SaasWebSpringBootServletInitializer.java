package com.arextest.web.saas;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
@ComponentScan(value = {"com.arextest.web"},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = {
            "com.arextest.web.api.service.interceptor.AuthorizationInterceptor",
            "com.arextest.web.api.service.interceptor.RefreshInterceptor"}),
    })
public class SaasWebSpringBootServletInitializer extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(SaasWebSpringBootServletInitializer.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(SaasWebSpringBootServletInitializer.class);
  }

}
