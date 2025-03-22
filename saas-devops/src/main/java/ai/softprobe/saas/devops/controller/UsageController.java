package ai.softprobe.saas.devops.controller;

import ai.softprobe.saas.devops.model.contract.QueryTenantUsageRequest;
import ai.softprobe.saas.devops.model.contract.QueryTenantUsageResponse;
import ai.softprobe.saas.devops.model.contract.UpdateSubScribeRequest;
import ai.softprobe.saas.devops.service.UsageService;
import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.common.utils.TenantContextUtil;
import com.arextest.web.model.contract.contracts.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wildeslam.
 * @create 2024/6/17 16:25
 */
@Slf4j
@Controller
@RequestMapping("/api/devops/usage")
public class UsageController {

  @Autowired
  private UsageService usageService;

  @PostMapping("/query")
  @ResponseBody
  public Response queryUsage(@RequestBody QueryTenantUsageRequest request) {
    TenantContextUtil.clear();
    QueryTenantUsageResponse response = new QueryTenantUsageResponse();
    response.setTotalBytes(usageService.queryUsage(request));
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/update/subscribe")
  @ResponseBody
  public Response queryUsage(@RequestBody UpdateSubScribeRequest request) {
    TenantContextUtil.setTenantCode(request.getTenantCode());
    SuccessResponse response = new SuccessResponse();
    response.setSuccess(usageService.updateSubScribe(request));
    return ResponseUtils.successResponse(response);
  }
}
