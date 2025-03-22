package ai.softprobe.saas.devops.controller;

import ai.softprobe.saas.devops.model.contract.AddUserRequest;
import ai.softprobe.saas.devops.model.contract.FinalizeSaasUserRequest;
import ai.softprobe.saas.devops.model.contract.InitSaasUserRequest;
import ai.softprobe.saas.devops.model.contract.RemoveUserRequest;
import ai.softprobe.saas.devops.service.UserManageService;
import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.common.utils.TenantContextUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wildeslam.
 * @create 2024/3/28 16:49
 */
@Slf4j
@Controller
@RequestMapping("/api/devops/user")
public class UserManageController {

  @Resource
  private UserManageService userManageService;

  @PostMapping("/initSaasUser")
  @ResponseBody
  public Response initSaasUser(@RequestBody InitSaasUserRequest request) {
    TenantContextUtil.setTenantCode(request.getTenantCode());
    return ResponseUtils.successResponse(userManageService.initSaasUser(request));
  }

  @PostMapping("/finalizeSaasUser")
  @ResponseBody
  public Response finalizeSaasUser(@RequestBody FinalizeSaasUserRequest request) {
    TenantContextUtil.setTenantCode(request.getTenantCode());
    return ResponseUtils.successResponse(userManageService.finalizeSaasUser(request));
  }

  @PostMapping("/addUser")
  @ResponseBody
  public Response addUser(@RequestBody AddUserRequest request) {
    TenantContextUtil.setTenantCode(request.getTenantCode());
    return ResponseUtils.successResponse(
        userManageService.addUser(request.getTenantCode(), request.getEmails()));
  }


  @PostMapping("/removeUser")
  @ResponseBody
  public Response removeUser(@RequestBody RemoveUserRequest request) {
    TenantContextUtil.setTenantCode(request.getTenantCode());
    return ResponseUtils.successResponse(
        userManageService.removeUser(request.getTenantCode(), request.getEmails()));
  }
}
