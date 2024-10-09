package com.arextest.storage.saas.api.service;

import com.arextest.storage.repository.impl.mongo.DesensitizationProvider;
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