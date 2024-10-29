package com.arextest.saas.api.model.contract.external;

import java.util.Set;
import lombok.Data;

/**
 * @author b_yu
 * @since 2024/7/24
 */
@Data
public class RemoveUserRequest {

  private String tenantCode;
  private Set<String> emails;
}
