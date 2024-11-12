package com.arextest.storage.saas.api.models.rr;

import java.util.Date;
import lombok.Data;

/**
 * @author: QizhengMo
 * @date: 2024/11/12 14:05
 */
@Data
public class RecordListingReq {
  private String appId;
  private Date from;
  private Date to;
}
