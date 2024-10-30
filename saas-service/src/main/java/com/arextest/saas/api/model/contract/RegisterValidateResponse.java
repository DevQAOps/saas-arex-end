package com.arextest.saas.api.model.contract;

import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/3/7 14:59
 */
@Data
public class RegisterValidateResponse extends SuccessResponseType {

  private String failedReason;
}
