package com.arextest.saas.api.model.contract;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wildeslam.
 * @create 2024/5/20 19:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClientDownloadResponse extends SuccessResponseType {

  private InnerClass windows;
  private InnerClass mac;

  @Data
  public static class InnerClass {

    private String armDownloadUrl;
    private String x64DownloadUrl;
  }
}
