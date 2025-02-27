package com.arextest.web.saas.api.controller.system;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.saas.api.service.SaasSystemConfigurationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saas/system")
public class SaasSystemConfigurationController {

  @Resource
  SaasSystemConfigurationService saasSystemConfigurationService;

  @GetMapping("/queryConfig")
  public Response queryConfig() {
    return ResponseUtils.successResponse(saasSystemConfigurationService.queryConfig());
  }


}
