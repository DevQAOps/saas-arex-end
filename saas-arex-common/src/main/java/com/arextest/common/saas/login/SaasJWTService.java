package com.arextest.common.saas.login;

import com.arextest.common.jwt.JWTServiceImpl;
import com.arextest.common.utils.TenantContextUtil;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Slf4j
@NoArgsConstructor
public class SaasJWTService extends JWTServiceImpl {

  private static final String KEY = "key";

  private static final String KEY_VALUE = "jwt_seed";

  private static final String SEED_KEY = "jwtSeed";

  private MongoTemplate mongoTemplate;

  public SaasJWTService(long accessExpireTime, long refreshExpireTime,
      MongoTemplate mongoTemplate) {
    super(accessExpireTime, refreshExpireTime, null);
    this.mongoTemplate = mongoTemplate;
  }

  private LoadingCache<String, JWTServiceImpl> JWTServiceImplCache =
      Caffeine.newBuilder().maximumSize(100).removalListener(((key, value, cause) -> {
        LOGGER.info("JWTServiceImpl expire, key : {}, cause : {}", key, cause);
      })).expireAfterWrite(2, TimeUnit.HOURS).build(new JWTServiceImplCacheLoader());

  @Override
  public String makeAccessToken(String username) {
    String tenantCode = TenantContextUtil.getTenantCode();
    JWTServiceImpl jwtServiceImpl = JWTServiceImplCache.get(tenantCode);
    if (jwtServiceImpl == null) {
      LOGGER.error("JWTServiceImpl not found, tenantCode : {}, action : {}", tenantCode,
          "makeAccessToken");
      return null;
    }
    return jwtServiceImpl.makeAccessToken(username);
  }

  @Override
  public String makeAccessToken(String username, long expireTime) {
    String tenantCode = TenantContextUtil.getTenantCode();
    JWTServiceImpl jwtServiceImpl = JWTServiceImplCache.get(tenantCode);
    if (jwtServiceImpl == null) {
      LOGGER.error("JWTServiceImpl not found, tenantCode : {}, action : {}", tenantCode,
          "makeAccessToken");
      return null;
    }
    return jwtServiceImpl.makeAccessToken(username, expireTime);
  }

  @Override
  public String makeRefreshToken(String username) {
    String tenantCode = TenantContextUtil.getTenantCode();
    JWTServiceImpl jwtServiceImpl = JWTServiceImplCache.get(tenantCode);
    if (jwtServiceImpl == null) {
      LOGGER.error("JWTServiceImpl not found, tenantCode : {}, action : {}", tenantCode,
          "makeRefreshToken");
      return null;
    }
    return jwtServiceImpl.makeRefreshToken(username);
  }

  @Override
  public boolean verifyToken(String field) {
    String tenantCode = TenantContextUtil.getTenantCode();
    JWTServiceImpl jwtServiceImpl = JWTServiceImplCache.get(tenantCode);
    if (jwtServiceImpl == null) {
      LOGGER.error("JWTServiceImpl not found, tenantCode : {}, action : {}", tenantCode, "verifyToken");
      return false;
    }
    return jwtServiceImpl.verifyToken(field);
  }

  public String getUserName(String token) {
    String tenantCode = TenantContextUtil.getTenantCode();
    JWTServiceImpl jwtServiceImpl = JWTServiceImplCache.get(tenantCode);
    if (jwtServiceImpl == null) {
      LOGGER.error("JWTServiceImpl not found, tenantCode : {}, action : {}", tenantCode, "getUserName");
      return null;
    }
    return jwtServiceImpl.getUserName(token);
  }

  // todo to get single-mongoTemplate from the multiple mongoTemplate
  private class JWTServiceImplCacheLoader implements CacheLoader<String, JWTServiceImpl> {

    @Nullable
    @Override
    public JWTServiceImpl load(@NonNull String key) {
      Query query = new Query().addCriteria(Criteria.where(KEY).is(KEY_VALUE));

      Document systemConfiguration = mongoTemplate.findOne(query, Document.class,
          "SystemConfiguration");
      if (systemConfiguration == null || StringUtils.isEmpty(
          systemConfiguration.getString(SEED_KEY))) {
        LOGGER.error("SystemConfiguration not found, key : {}", key);
        return null;
      }
      return new JWTServiceImpl(getAccessExpireTime(), getRefreshExpireTime(),
          systemConfiguration.getString(SEED_KEY));
    }
  }

}