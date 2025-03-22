package ai.softprobe.web.saas.api.controller.login;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import ai.softprobe.web.saas.api.service.SaasLoginService;
import ai.softprobe.web.saas.model.contract.SaasVerifyRequestType;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saas/login")
public class SaasLoginController {

  @Resource
  SaasLoginService saasLoginService;

  @GetMapping("/verify")
  public Response verify(@Valid SaasVerifyRequestType requestType) {
    return ResponseUtils.successResponse(
        saasLoginService.verify(requestType)
    );
  }

}
