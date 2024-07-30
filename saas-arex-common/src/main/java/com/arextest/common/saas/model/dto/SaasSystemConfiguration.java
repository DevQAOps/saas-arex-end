package com.arextest.common.saas.model.dto;

import com.arextest.common.saas.model.dao.SaasSystemConfigurationCollection.SubscribeInfo;
import com.arextest.config.model.dto.system.SystemConfiguration;
import lombok.Data;

@Data
public class SaasSystemConfiguration extends SystemConfiguration {

  private String tenantToken;
  private SubscribeInfo subscribeInfo;
}
