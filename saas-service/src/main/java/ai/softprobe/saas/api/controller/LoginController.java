package ai.softprobe.saas.api.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import ai.softprobe.saas.api.common.enums.ErrorCode;
import ai.softprobe.saas.api.common.exceptions.SpSaasException;
import ai.softprobe.saas.api.common.utils.CommonUtil;
import ai.softprobe.saas.api.common.utils.JwtUtil;
import ai.softprobe.saas.api.service.OauthHandler;
import ai.softprobe.saas.api.service.TenantService;
import ai.softprobe.saas.api.model.contract.BindTenantRequest;
import ai.softprobe.saas.api.model.contract.DeleteUserRequest;
import ai.softprobe.saas.api.model.contract.LogOffRequest;
import ai.softprobe.saas.api.model.contract.LoginRequest;
import ai.softprobe.saas.api.model.contract.LoginResponse;
import ai.softprobe.saas.api.model.contract.OauthLoginRequest;
import ai.softprobe.saas.api.model.contract.OauthLoginResponse;
import ai.softprobe.saas.api.model.contract.QueryUserInfoResponse;
import ai.softprobe.saas.api.model.contract.RegisterRequest;
import ai.softprobe.saas.api.model.contract.RegisterResponse;
import ai.softprobe.saas.api.model.contract.RegisterValidateResponse;
import ai.softprobe.saas.api.model.contract.ResetPasswordRequest;
import ai.softprobe.saas.api.model.contract.SendResetPwdEmailRequest;
import ai.softprobe.saas.api.model.contract.SuccessResponseType;
import ai.softprobe.saas.api.model.contract.VerifyRequest;
import ai.softprobe.saas.api.model.contract.VerifyResponse;
import ai.softprobe.saas.api.model.vo.TenantVo;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wildeslam.
 * @create 2024/3/7 14:56
 */
@Slf4j
@Controller
@RequestMapping("/api/login")
public class LoginController {

  @Autowired
  private TenantService tenantService;

  @Autowired
  private OauthHandler oauthHandler;


  @Deprecated
  @PostMapping("/validate")
  @ResponseBody
  public Response validate(@Valid @RequestBody RegisterRequest request) {
    RegisterValidateResponse response = new RegisterValidateResponse();
    response.setSuccess(false);
    if (!tenantService.validate(request.getEmail())) {
      response.setFailedReason("email has been registered");
      return ResponseUtils.successResponse(response);
    }
    if (!request.getEmail().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
      response.setFailedReason("email format error");
      return ResponseUtils.successResponse(response);
    }
    response.setSuccess(true);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/register")
  @ResponseBody
  public Response register(@Valid @RequestBody RegisterRequest request) {
    RegisterResponse response = new RegisterResponse();
    try {
      response.setSuccess(tenantService.register(request));
    } catch (SpSaasException e) {
      LOGGER.error("register error", e);
      response.setSuccess(false);
      response.setErrorCode(e.getCode());
    }

    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/verify")
  @ResponseBody
  public Response verify(@Valid @RequestBody VerifyRequest request) {
    VerifyResponse response = new VerifyResponse();
    try {
      response.setAccessToken(tenantService.verify(request));
      response.setSuccess(true);
    } catch (SpSaasException e) {
      LOGGER.error("verify error", e);
      response.setSuccess(false);
      response.setErrorCode(e.getCode());
    }
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/login")
  @ResponseBody
  public Response login(@Valid @RequestBody LoginRequest request) {

    LoginResponse response = new LoginResponse();
    try {
      String token = tenantService.login(request);
      response.setSuccess(true);
      response.setAccessToken(token);
    } catch (SpSaasException e) {
      LOGGER.error("login error", e);
      response.setSuccess(false);
      response.setErrorCode(e.getCode());
    }
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/delete")
  @ResponseBody
  public Response login(@Valid @RequestBody DeleteUserRequest request) {
    return ResponseUtils.successResponse(tenantService.deleteTenant(request.getEmail()));
  }

  @PostMapping("/oauthLogin")
  @ResponseBody
  public Response oauthLogin(@Valid @RequestBody OauthLoginRequest request) {
    OauthLoginResponse response = oauthHandler.oauthLogin(request);
    response.setSuccess(true);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/bind")
  @ResponseBody
  public Response bind(@RequestHeader("access-token") String accessToken,
      @Valid @RequestBody BindTenantRequest request) {
    String email = JwtUtil.getUserNameByEmailToken(accessToken);
    LoginResponse response = new LoginResponse();
    try {
      boolean success = tenantService.bind(email, request.getTenantName(),
          request.getTenantCode());
      response.setSuccess(success);
      if (success) {
        response.setAccessToken(JwtUtil.makeAccessTokenWithTenantCode(request.getTenantCode()));
      }
    } catch (SpSaasException e) {
      LOGGER.error("bind error", e);
      response.setSuccess(false);
      response.setErrorCode(e.getCode());
    }
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/logOff")
  @ResponseBody
  public Response logOff(@RequestHeader(name = "access-token") String accessToken,
      @Valid @RequestBody LogOffRequest request) {
    String tenantCode = JwtUtil.getUserNameByUserToken(accessToken);
    CommonUtil.checkTenantCode(tenantCode);
    SuccessResponseType responseType = new SuccessResponseType();
    if (!Objects.equals(tenantCode, request.getTenantCode())) {
      responseType.setErrorCode(ErrorCode.COMPANY_CODE_ERROR.getCodeValue());
      responseType.setSuccess(false);
    }
    try {
      boolean success = tenantService.logOff(request.getTenantCode());
      responseType.setSuccess(success);
    } catch (SpSaasException e) {
      LOGGER.error("logOff error", e);
      responseType.setSuccess(false);
      responseType.setErrorCode(e.getCode());
    }
    return ResponseUtils.successResponse(responseType);
  }

  @PostMapping("/queryUser")
  @ResponseBody
  public Response queryUser(@RequestHeader("access-token") String accessToken) {
    TenantVo user = tenantService.getTenant(accessToken);
    QueryUserInfoResponse response = new QueryUserInfoResponse();
    response.setSuccess(user != null);
    response.setUser(user);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/queryTenant")
  @ResponseBody
  public Response queryTenant(@RequestHeader("access-token") String accessToken) {
    TenantVo user = tenantService.getTenant(accessToken);
    QueryUserInfoResponse response = new QueryUserInfoResponse();
    response.setSuccess(user != null);
    response.setUser(user);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/sendResetPwdEmail")
  @ResponseBody
  public Response sendResetPwdEmail(@RequestBody SendResetPwdEmailRequest request) {
    return ResponseUtils.successResponse(tenantService.sendResetPwdEmail(request.getEmail()));
  }

  @PostMapping("/resetPassword")
  @ResponseBody
  public Response resetPassword(@RequestHeader("access-token") String accessToken,
      @RequestBody ResetPasswordRequest request) {
    LoginResponse response = new LoginResponse();
    String email = JwtUtil.getUserNameByEmailToken(accessToken);
    return ResponseUtils.successResponse(tenantService.resetPassword(email, request.getPassword()));
  }
}
