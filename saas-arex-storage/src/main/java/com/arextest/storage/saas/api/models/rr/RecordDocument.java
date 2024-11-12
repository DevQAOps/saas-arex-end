package com.arextest.storage.saas.api.models.rr;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author: QizhengMo
 * @date: 2024/11/12 11:34
 */
@Data
@FieldNameConstants
@Document(collection = "rr_record")
public class RecordDocument {
  @Id
  private String recordId;
  private String appId;
  private Date createTime;
  private Date updateTime;

  private List<Event> events;
  @Data
  public static class Event {
    private int type;
    private long timestamp;
    private String data;
  }
}
