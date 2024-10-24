package com.arextest.storage.saas.api.models.traffic;

import com.arextest.model.replay.PagedRequestType;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: QizhengMo
 * @date: 2024/9/26 14:39
 */
@Getter
@Setter
public class CaseSummaryRequest extends PagedRequestType {
  private List<Filter> filters;

  @Data
  public static class Filter {
    private FilterType filterType;
    private String key;
    private String value;
  }

  public enum FilterType {
    ENDPOINT,
    ;
  }
}
