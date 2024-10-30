package com.arextest.saas.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {"com.arextest.saas"}, exclude = {
    SecurityAutoConfiguration.class, RedisAutoConfiguration.class})
public class ArexSaasApiApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(ArexSaasApiApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(ArexSaasApiApplication.class);
  }
}
