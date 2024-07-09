package com.arextest.saasdevops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {"com.arextest.common.saas", "com.arextest.saasdevops"})
public class SaasDevOpsApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SaasDevOpsApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SaasDevOpsApplication.class);
    }

}
