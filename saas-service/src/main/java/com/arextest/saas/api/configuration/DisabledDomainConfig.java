package com.arextest.saas.api.configuration;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wildeslam.
 * @create 2024/6/24 10:52
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "arex.domain")
public class DisabledDomainConfig {

  private Set<String> disabled = new HashSet<>();
}