package com.arextest.saasdevops.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.TenantContextUtil;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.saasdevops.contract.AddUserRequest;
import com.arextest.saasdevops.contract.InitSaasUserRequest;
import com.arextest.saasdevops.contract.RemoveUserRequest;
import com.arextest.saasdevops.service.UserManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

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
        TenantContextUtil.setTenantCode(request.getCompanyName());
        return ResponseUtils.successResponse(userManageService.initSaasUser(request.getCompanyName(), request.getEmail()));
    }

    @PostMapping("/addUser")
    @ResponseBody
    public Response addUser(@RequestBody AddUserRequest request) {
        TenantContextUtil.setTenantCode(request.getCompanyName());
        return ResponseUtils.successResponse(userManageService.addUser(request.getCompanyName(), request.getEmails()));
    }


    @PostMapping("/removeUser")
    @ResponseBody
    public Response removeUser(@RequestBody RemoveUserRequest request) {
        TenantContextUtil.setTenantCode(request.getCompanyName());
        return ResponseUtils.successResponse(userManageService.removeUser(request.getCompanyName(), request.getEmails()));
    }
}
