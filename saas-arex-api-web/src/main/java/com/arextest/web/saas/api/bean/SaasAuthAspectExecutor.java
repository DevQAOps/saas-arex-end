package com.arextest.web.saas.api.bean;

import com.arextest.common.jwt.JWTService;
import com.arextest.common.utils.TenantContextUtil;
import com.arextest.config.repository.impl.ApplicationConfigurationRepositoryImpl;
import com.arextest.config.repository.impl.SystemConfigurationRepositoryImpl;
import com.arextest.web.api.service.aspect.AppAuthAspectExecutor;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SaasAuthAspectExecutor extends AppAuthAspectExecutor {

  private static final String KEY = "key";

  private static final String AUTH_SWITCH = "auth_switch";

  private static final String AUTH_FIELD_NAME = "authSwitch";

  @Resource
  private MongoTemplate mongoTemplate;

  private LoadingCache<String, Boolean> authSwitchCache =
      Caffeine.newBuilder().maximumSize(100).removalListener(((key, value, cause) -> {
            LOGGER.info("authSwitch expire, key : {}, cause : {}", key, cause);
          }))
          .expireAfterWrite(2, TimeUnit.HOURS)
          .build(new AuthSwitchCacheLoader());

  @Autowired
  public SaasAuthAspectExecutor(
      ApplicationConfigurationRepositoryImpl applicationConfigurationRepository,
      SystemConfigurationRepositoryImpl systemConfigurationRepository, JWTService jwtService) {
    super(applicationConfigurationRepository, systemConfigurationRepository, jwtService);
  }


  @Override
  protected boolean judgeByAuth() {
    String tenantCode = TenantContextUtil.getTenantCode();
    return Boolean.TRUE.equals(authSwitchCache.get(tenantCode));
  }


  private class AuthSwitchCacheLoader implements CacheLoader<String, Boolean> {

    @NonNull
    @Override
    public Boolean load(@NonNull String key) {
      Query query = new Query().addCriteria(Criteria.where(KEY).is(AUTH_SWITCH));

      Document systemConfiguration = mongoTemplate.findOne(query, Document.class,
          "SystemConfiguration");
      if (systemConfiguration == null || systemConfiguration.getBoolean(AUTH_FIELD_NAME) == null) {
        LOGGER.error("SystemConfiguration not found, key : {}", key);
        return false;
      }
      return systemConfiguration.getBoolean(AUTH_FIELD_NAME);
    }
  }
}
