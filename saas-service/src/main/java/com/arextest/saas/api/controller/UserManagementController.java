package com.arextest.saas.api.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.saas.api.common.enums.ErrorCode;
import com.arextest.saas.api.common.utils.CommonUtil;
import com.arextest.saas.api.common.utils.JwtUtil;
import com.arextest.saas.api.service.TenantService;
import com.arextest.saas.api.model.contract.QueryUsersResponse;
import com.arextest.saas.api.model.contract.SuccessResponseType;
import com.arextest.saas.api.model.contract.UserMgntRequest;
import com.arextest.saas.api.model.vo.TenantVo;
import jakarta.validation.Valid;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wildeslam.
 * @create 2024/4/25 15:55
 */
@Slf4j
@Controller
@RequestMapping("/api/user/mgnt")
public class UserManagementController {

  @Autowired
  private TenantService tenantService;

  @Deprecated
  @GetMapping("/queryTenants/{email}")
  @ResponseBody
  public Response queryTenants(@PathVariable String email) {
    return ResponseUtils.successResponse(tenantService.queryTenantsByEmail(email)
        .stream().map(TenantVo::getTenantCode).collect(Collectors.toSet()));
  }


  @PostMapping("/addUser")
  @ResponseBody
  public Response add(@RequestHeader(name = "access-token") String accessToken,
      @Valid @RequestBody UserMgntRequest request) {
    String tenantCode = JwtUtil.getUserNameByUserToken(accessToken);
    CommonUtil.checkTenantCode(tenantCode);
    SuccessResponseType responseType = new SuccessResponseType();
    for (String email : request.getEmails()) {
      if (!CommonUtil.validateEmail(email)) {
        responseType.setErrorCode(ErrorCode.EMAIL_FORMAT_ERROR.getCodeValue());
        responseType.setSuccess(false);
        return ResponseUtils.successResponse(responseType);
      }
    }
    responseType.setSuccess(tenantService.addUsers(tenantCode, request.getEmails()));
    return ResponseUtils.successResponse(responseType);
  }

  @PostMapping("/removeUser")
  @ResponseBody
  public Response remove(@RequestHeader(name = "access-token") String accessToken,
      @Valid @RequestBody UserMgntRequest request) {
    String tenantCode = JwtUtil.getUserNameByUserToken(accessToken);
    CommonUtil.checkTenantCode(tenantCode);
    SuccessResponseType responseType = new SuccessResponseType();
    responseType.setSuccess(tenantService.removeUsers(tenantCode, request.getEmails()));
    return ResponseUtils.successResponse(responseType);
  }

  @PostMapping("/queryUserEmails")
  @ResponseBody
  public Response queryUsers(@RequestHeader(name = "access-token") String accessToken) {
    String tenantCode = JwtUtil.getUserNameByUserToken(accessToken);
    CommonUtil.checkTenantCode(tenantCode);
    QueryUsersResponse responseType = new QueryUsersResponse();
    responseType.setSuccess(true);
    responseType.setUserEmails(tenantService.queryUserEmailsByCode(tenantCode));
    return ResponseUtils.successResponse(responseType);
  }
}
