package com.arextest.saas.api.model.contract;

import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/3/5 15:34
 */
@Data
public class SuccessResponseType {

  private Boolean success;
  private int errorCode;
}
