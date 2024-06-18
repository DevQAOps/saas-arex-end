package com.arextest.saasdevops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.arextest.common.saas", "com.arextest.saasdevops"})
public class SaasDevOpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasDevOpsApplication.class, args);
    }

}
