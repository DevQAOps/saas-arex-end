package com.arextest.saas.api.model.contract;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/3/5 15:28
 */
@Data
public class LoginRequest {

  @NotNull
  private String email;
  @NotNull
  private String password;
}
