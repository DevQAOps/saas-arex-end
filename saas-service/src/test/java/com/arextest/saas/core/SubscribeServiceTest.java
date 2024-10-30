package com.arextest.saas.core;

import com.arextest.saas.api.ArexSaasApiApplication;
import com.arextest.saas.api.controller.UserManagementController;
import com.arextest.saas.api.service.SubscribeService;
import com.arextest.saas.api.model.dto.TenantDto;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest(classes = ArexSaasApiApplication.class)
public class SubscribeServiceTest {

  @Resource
  SubscribeService subscribeService;
  @Autowired
  private UserManagementController userManagementController;

  @Test
  public void testSubscribePlan() {
    TenantDto tenantDto = new TenantDto();
    tenantDto.setTenantCode("ddd");
    tenantDto.setTenantToken(
        "ddd:TsdfKIWSi2PxSPU7vlE5kCQfbqT/pKOXjnvPRI8Lq3wFmCKKR4hu/kf1mZQiLqUY");
    tenantDto.setEmail("corychen13@gmail.com");
    tenantDto.setExpireTime(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365);

    subscribeService.initDataBySubscription(tenantDto);
  }
}
