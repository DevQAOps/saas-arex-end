package com.arextest.saas.api.configuration;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wildeslam.
 * @create 2024/3/13 17:13
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "secure.ignored")
public class IgnoreUrlsConfig {

  private List<String> urls = new ArrayList<>();
}
