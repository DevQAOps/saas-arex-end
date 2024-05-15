package com.arextest.saasdevops;

import com.arextest.common.saas.tenant.TenantRedisHandler;
import com.arextest.common.saas.tenant.TenantStatusRedisInfo;
import com.arextest.saasdevops.model.dto.TenantStatusInfo;
import com.arextest.saasdevops.service.TenantManageService;
import java.util.Calendar;
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
    tenantStatusInfo.setExpireTime(
        getExpireTime(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30));
    tenantManageService.initTenantStatus(tenantStatusInfo);

    TenantStatusRedisInfo test = tenantRedisHandler.getTenantStatus("whb1");
    Assertions.assertEquals(
        "a+aBZI466WklECMCkWte4lYBfWOxC95hOKHUmXXJxFsv93SdyHtmp0RLIunpoRfVypTloF4o9OTFlhTVRlLXmw==",
        test.getTenantToken());

  }

  private long getExpireTime(long expireTime) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(expireTime);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return calendar.getTimeInMillis();
  }

}
