package com.arextest.storage.saas.api.models.rr;

import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author: QizhengMo
 * @date: 2024/11/12 14:07
 */
@Data
public class RecordDto {
  private String recordId;
  private Date createTime;
  private Date updateTime;
  private List<EventDto> events;

  private String appId;
  private String userId;
  private String clientId;
  private String mobileNo;
  private Map<String, String> ext;

  @Data
  public static class EventDto {
    private int type;
    private long timestamp;
    private Object data;
  }
}
