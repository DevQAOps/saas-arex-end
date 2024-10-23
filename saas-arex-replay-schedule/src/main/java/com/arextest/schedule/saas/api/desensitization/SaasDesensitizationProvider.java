package com.arextest.schedule.saas.api.desensitization;

import com.arextest.schedule.serialization.DesensitizationProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.stereotype.Component;

@Component
public class SaasDesensitizationProvider extends DesensitizationProvider {

  public SaasDesensitizationProvider(MongoDatabaseFactory factory) {
    super(factory.getMongoDatabase());
  }

  @Value("${arex.saas.desensitization.url}")
  private String desensitizationUrl;


  @Override
  protected String getJarUrl() {
    return desensitizationUrl;
  }
}