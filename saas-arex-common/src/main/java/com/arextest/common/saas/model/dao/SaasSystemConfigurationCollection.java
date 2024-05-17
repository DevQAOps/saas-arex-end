package com.arextest.common.saas.model.dao;

import com.arextest.config.model.dao.config.SystemConfigurationCollection;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@FieldNameConstants
@Data
public class SaasSystemConfigurationCollection extends SystemConfigurationCollection {

  private String tenantToken;
}
