package com.arextest.saas.core;

import com.arextest.saas.api.ArexSaasApiApplication;
import com.arextest.saas.api.service.CompanyTokenService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest(classes = ArexSaasApiApplication.class)
public class CompanyTokenServiceTest {

  @Resource
  CompanyTokenService companyTokenService;

  @Test
  public void testGenerateToken() throws Exception {
    String s = companyTokenService.generateToken("ctrip");
    System.out.println(s);
  }


}
