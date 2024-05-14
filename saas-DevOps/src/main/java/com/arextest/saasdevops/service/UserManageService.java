package com.arextest.saasdevops.service;

import com.arextest.saasdevops.model.contract.InitSaasUserRequest;
import java.util.List;

/**
 * @author wildeslam.
 * @create 2024/3/29 14:59
 */
public interface UserManageService {

  /**
   * Initialize the organization's resources including the database, indexes and add admin user.
   * return true if the initialization is successful.
   */
  boolean initSaasUser(InitSaasUserRequest request);

  /**
   * Add users to the organization. return true if the addition is successful.
   */
  boolean addUser(String tenantCode, List<String> emails);

  /**
   * Remove users to the organization. return true if the removal is successful.
   */
  boolean removeUser(String tenantCode, List<String> emails);
}
