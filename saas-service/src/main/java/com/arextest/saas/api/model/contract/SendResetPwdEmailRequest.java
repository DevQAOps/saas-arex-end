package com.arextest.saas.api.model.contract;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author b_yu
 * @since 2024/8/9
 */
@Data
public class SendResetPwdEmailRequest {

  @NotNull(message = "email is required")
  private String email;
}
