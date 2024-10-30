package com.arextest.saas.api.model.contract;

import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/3/5 15:28
 */
@Data
public class VerifyResponse extends SuccessResponseType {

  private String accessToken;
}
