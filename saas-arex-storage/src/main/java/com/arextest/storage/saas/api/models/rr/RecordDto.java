package com.arextest.storage.saas.api.models.rr;

import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: QizhengMo
 * @date: 2024/11/12 14:07
 */
@Data
@NoArgsConstructor
public class RecordDto {
  @NotBlank(message = "Record ID cannot be empty.")
  private String recordId;
  private Date createTime;
  private Date updateTime;
  private List<EventDto> events;

  @NotBlank(message = "App ID cannot be empty.")
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

  @Data
  public static class Search {
    private String recordId;
    private String appId;
    private String userId;
    private String clientId;
    private String mobileNo;
    private Map<String, String> ext;
    private Date createTimeFrom;
    private Date createTimeTo;
  }
}
