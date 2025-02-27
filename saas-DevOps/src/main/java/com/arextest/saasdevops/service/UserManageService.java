package com.arextest.saasdevops.service;

import com.arextest.saasdevops.model.contract.FinalizeSaasUserRequest;
import com.arextest.saasdevops.model.contract.InitSaasUserRequest;
import com.arextest.saasdevops.model.contract.UserType;
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
   * Finalize the organization's resources including the database, indexes and remove admin user.
   * return true if the finalization is successful.
   */
  boolean finalizeSaasUser(FinalizeSaasUserRequest request);

  /**
   * Add users to the organization. return true if the addition is successful.
   */
  boolean addUser(String tenantCode, List<UserType> emails);

  /**
   * Remove users to the organization. return true if the removal is successful.
   */
  boolean removeUser(String tenantCode, List<String> emails);
}
