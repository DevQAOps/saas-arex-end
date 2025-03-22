package ai.softprobe.saas.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {"ai.softprobe.saas"}, exclude = {
    SecurityAutoConfiguration.class, RedisAutoConfiguration.class})
public class SaasApiApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(SaasApiApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(SaasApiApplication.class);
  }
}
