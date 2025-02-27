package com.arextest.common.saas.model.dao;

import com.arextest.config.model.dao.config.SystemConfigurationCollection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@FieldNameConstants
@Document("SystemConfiguration")
public class SaasSystemConfigurationCollection extends SystemConfigurationCollection {

  private String tenantToken;
  private SubscribeInfo subscribeInfo;

  @Data
  @AllArgsConstructor
  public static class SubscribeInfo {

    private Long trafficLimit;
    private Long start;
    private Long end;
  }
}
