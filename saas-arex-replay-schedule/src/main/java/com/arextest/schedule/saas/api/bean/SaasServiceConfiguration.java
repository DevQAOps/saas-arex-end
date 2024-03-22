package com.arextest.schedule.saas.api.bean;

import com.arextest.common.jwt.JWTService;
import com.arextest.common.saas.login.SaasJWTService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class SaasServiceConfiguration {

  private static final long ACCESS_EXPIRE_TIME = 604800000L;
  private static final long REFRESH_EXPIRE_TIME = 2592000000L;

  @Bean
  public JWTService saasJWTService(MongoTemplate mongoTemplate) {
    return new SaasJWTService(ACCESS_EXPIRE_TIME, REFRESH_EXPIRE_TIME, mongoTemplate);
  }

}
