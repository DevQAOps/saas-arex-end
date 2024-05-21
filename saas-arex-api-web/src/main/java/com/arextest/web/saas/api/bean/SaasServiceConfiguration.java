package com.arextest.web.saas.api.bean;

import com.arextest.common.jwt.JWTService;
import com.arextest.common.saas.httpclient.AccessRequestInterceptor;
import com.arextest.common.saas.login.SaasJWTService;
import com.arextest.common.saas.repository.SaasSystemConfigurationRepository;
import com.arextest.common.saas.repository.impl.SaasSystemConfigurationRepositoryImpl;
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
   * @param mongoTemplate the multi-source mongo template
   * @return
   */
  @Bean
  public JWTService saasJWTService(MongoTemplate mongoTemplate) {
    return new SaasJWTService(ACCESS_EXPIRE_TIME, REFRESH_EXPIRE_TIME, mongoTemplate);
  }

  /**
   * for http request interceptor
   *
   * @param jwtService: the multi-source for the JWTService
   * @param interfaceAddressConfiguration
   * @return
   */
  @Bean
  public ClientHttpRequestInterceptor accessRequestInterceptor(JWTService jwtService,
      InterfaceAddressConfiguration interfaceAddressConfiguration) {
    return new AccessRequestInterceptor(jwtService,
        interfaceAddressConfiguration.getServiceAddressInfos());
  }

  @Bean
  public SaasSystemConfigurationRepository saasSystemConfigurationRepository(MongoTemplate mongoTemplate) {
    return new SaasSystemConfigurationRepositoryImpl(mongoTemplate);
  }


  @Configuration
  public class InterfaceAddressConfiguration {

    @Value("${arex.storage.service.url}")
    private String storageServiceUrl;


    @Value("${arex.schedule.service.url}")
    private String scheduleServiceUrl;

    public Set<String> getServiceAddressInfos() {
      return new HashSet<>(Arrays.asList(storageServiceUrl, scheduleServiceUrl));
    }
  }

}
