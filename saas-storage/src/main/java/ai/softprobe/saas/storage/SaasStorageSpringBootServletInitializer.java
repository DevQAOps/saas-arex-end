package ai.softprobe.saas.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication(scanBasePackages = {"com.arextest.storage",
    "ai.softprobe.saas.common"}, exclude = {
    MongoAutoConfiguration.class})
public class SaasStorageSpringBootServletInitializer extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(SaasStorageSpringBootServletInitializer.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(SaasStorageSpringBootServletInitializer.class);
  }
}
