package com.arextest.storage.saas.api.models.traffic;

import java.util.Date;
import java.util.Map;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

/**
 * @author: QizhengMo
 * @date: 2024/9/19 20:31
 */
@Data
@FieldNameConstants
public class TrafficCase {
  @Id
  private String recordId;
  private String operationName;
  private Date creationTime;
  private Map<String, String> tags;

  @Transient
  private String type;
}
