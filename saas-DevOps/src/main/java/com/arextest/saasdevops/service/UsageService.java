package com.arextest.saasdevops.service;

/**
 * @author wildeslam.
 * @create 2024/6/17 16:28
 */
public interface UsageService {
  Long queryUsage(String tenantCode, Boolean in);
}
