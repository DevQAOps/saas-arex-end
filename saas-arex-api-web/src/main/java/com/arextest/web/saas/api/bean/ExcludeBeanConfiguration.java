package com.arextest.web.saas.api.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExcludeBeanConfiguration {


  @Bean(name = "oldDataCleaner")
  public SaasOldDataCleaner oldDataCleaner() {
    return new SaasOldDataCleaner();
  }


  private static class SaasOldDataCleaner {

  }


}
