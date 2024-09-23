package com.arextest.schedule.saas.api.bean;

import com.arextest.common.jwt.JWTService;
import com.arextest.common.saas.httpclient.AccessRequestInterceptor;
import com.arextest.common.saas.httpclient.SaasServiceRequestInterceptor;
import com.arextest.common.saas.login.SaasJWTService;
import com.arextest.common.saas.login.SaasServiceJWTService;
import com.arextest.extension.desensitization.DataDesensitization;
import com.arextest.extension.desensitization.DefaultDataDesensitization;
import com.arextest.schedule.comparer.CompareConfigService;
import com.arextest.schedule.comparer.CompareService;
import com.arextest.schedule.saas.api.compare.SaasCompareService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;

@Configuration
public class SaasServiceConfiguration {

  private static final long ACCESS_EXPIRE_TIME = 604800000L;
  private static final long REFRESH_EXPIRE_TIME = 2592000000L;

  /**
   * for jwt token verfication
   *
   * @param mongoTemplate
   * @return
   */
  @Bean
  public JWTService saasJWTService(MongoTemplate mongoTemplate) {
    return new SaasJWTService(ACCESS_EXPIRE_TIME, REFRESH_EXPIRE_TIME, mongoTemplate);
  }

  /**
   * for producing jwttoken to access arex-saas-service
   *
   * @param tokenSecret the multi-source token secret
   * @return
   */
  @Bean
  public SaasServiceJWTService saasServiceJWTService(
      @Value("${saas.service.accessSecret}") String tokenSecret) {
    return new SaasServiceJWTService(tokenSecret);
  }

  /*
   * for saas service request interceptor
   */
  @Bean
  public ClientHttpRequestInterceptor saasServiceRequestInterceptor(
      SaasServiceJWTService jwtService,
      InterfaceAddressConfiguration interfaceAddressConfiguration) {
    return new SaasServiceRequestInterceptor(jwtService,
        interfaceAddressConfiguration.getSaasServiceAddressInfos());
  }

  /**
   * for http request interceptor
   *
   * @param jwtService
   * @param interfaceAddressConfiguration
   * @return
   */
  @Bean
  public ClientHttpRequestInterceptor accessRequestInterceptor(JWTService jwtService,
      InterfaceAddressConfiguration interfaceAddressConfiguration) {
    return new AccessRequestInterceptor(jwtService,
        interfaceAddressConfiguration.getServiceAddressInfos());
  }

  /**
   * for compare service
   *
   * @param compareConfigService
   * @return
   */
  @Bean
  public CompareService compareService(CompareConfigService compareConfigService) {
    return new SaasCompareService(compareConfigService);
  }

  @Bean
  DataDesensitization desensitizationService() {
    return new DefaultDataDesensitization();
  }

  @Configuration
  public class InterfaceAddressConfiguration {

    @Value("${arex.api.service.api}")
    private String apiServiceUrl;

    @Value("${arex.storage.service.api}")
    private String storageServiceUrl;


    @Value("${arex.schedule.service.api}")
    private String scheduleServiceUrl;

    @Value("${saas.service.domain}")
    private String saasServiceDomain;


    public Set<String> getServiceAddressInfos() {
      return new HashSet<>(Arrays.asList(apiServiceUrl, storageServiceUrl, scheduleServiceUrl));
    }

    public Set<String> getSaasServiceAddressInfos() {
      return new HashSet<>(Arrays.asList(saasServiceDomain));
    }

  }
}
