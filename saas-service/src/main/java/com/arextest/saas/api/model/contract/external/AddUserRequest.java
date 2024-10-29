package com.arextest.saas.api.model.contract.external;

import java.util.List;
import lombok.Data;

/**
 * @author b_yu
 * @since 2024/7/24
 */
@Data
public class AddUserRequest {

  private String tenantCode;
  private List<UserType> emails;
}
