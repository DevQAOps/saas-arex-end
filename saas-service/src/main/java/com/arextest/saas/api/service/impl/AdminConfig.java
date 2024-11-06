package com.arextest.saas.api.service.impl;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: QizhengMo
 * @date: 2024/11/6 19:06
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "arex.oauth")
public class AdminConfig {
  private Set<String> admins;
}
