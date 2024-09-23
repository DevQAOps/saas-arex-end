package com.arextest.storage.saas.api.models.traffic;

import java.util.Date;
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

  @Transient
  private String type;
}
