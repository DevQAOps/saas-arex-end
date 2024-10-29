package com.arextest.saas.api.model.contract;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/4/26 15:42
 */
@Data
public class UserMgntRequest {

  @NotNull
  private Set<String> emails;
}
