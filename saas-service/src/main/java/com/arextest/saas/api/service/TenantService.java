package com.arextest.saas.api.service;

import com.arextest.saas.api.model.contract.LoginRequest;
import com.arextest.saas.api.model.contract.RegisterRequest;
import com.arextest.saas.api.model.contract.VerifyRequest;
import com.arextest.saas.api.model.vo.TenantVo;
import java.util.List;
import java.util.Set;

/**
 * @author wildeslam.
 * @create 2024/3/5 13:44
 */
public interface TenantService {

  boolean validate(String email);

  boolean register(RegisterRequest request);

  String verify(VerifyRequest request);

  boolean deleteTenant(String tenantCode);

  /**
   * @return token
   */
  String login(LoginRequest request);

  boolean bind(String email, String tenantName, String tenantCode);

  TenantVo getTenant(String accessToken);

  /**
   * Add users to the organization. return true if the addition is successful.
   */
  boolean addUsers(String tenantCode, Set<String> emails);

  /**
   * Remove users to the organization. return true if the removal is successful.
   */
  boolean removeUsers(String tenantCode, Set<String> emails);

  boolean logOff(String tenantCode);

  List<TenantVo> queryTenantsByEmail(String email);

  Set<String> queryUserEmailsByCode(String tenantCode);

  boolean sendResetPwdEmail(String email);

  boolean resetPassword(String email, String password);
}
