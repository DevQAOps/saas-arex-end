package com.arextest.saasdevops;

import com.arextest.common.saas.tenant.TenantRedisHandler;
import com.arextest.common.saas.tenant.TenantStatusRedisInfo;
import com.arextest.saasdevops.model.dto.TenantStatusInfo;
import com.arextest.saasdevops.service.TenantManageService;
import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest(classes = SaasDevOpsApplication.class)
public class TenantManageServiceTest {

  @Resource
  TenantManageService tenantManageService;

  @Resource
  TenantRedisHandler tenantRedisHandler;

  @Test
  public void testInitTenantStatus() {
    TenantStatusInfo tenantStatusInfo = new TenantStatusInfo();
    tenantStatusInfo.setTenantCode("whb1");
    tenantStatusInfo.setTenantToken(
        "a+aBZI466WklECMCkWte4lYBfWOxC95hOKHUmXXJxFsv93SdyHtmp0RLIunpoRfVypTloF4o9OTFlhTVRlLXmw==");
    tenantStatusInfo.setTenantStatus(0);
    tenantManageService.initTenantStatus(tenantStatusInfo);

    TenantStatusRedisInfo test = tenantRedisHandler.getTenantStatus("whb1");
    Assertions.assertEquals(
        "a+aBZI466WklECMCkWte4lYBfWOxC95hOKHUmXXJxFsv93SdyHtmp0RLIunpoRfVypTloF4o9OTFlhTVRlLXmw==",
        test.getTenantToken());

  }

}
