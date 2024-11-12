package com.arextest.storage.saas.api.models.rr;

import com.arextest.storage.saas.api.models.rr.RecordDto.EventDto;
import java.util.List;
import lombok.Data;

/**
 * @author: QizhengMo
 * @date: 2024/11/12 11:30
 */
@Data
public class RecordingReq {
  private String appId;
  private String recordId;
  private List<EventDto> events;
}
