package com.arextest.saas.api.model.contract;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/3/5 15:23
 */
@Data
public class VerifyRequest {

  @NotNull
  private String upn;
}
