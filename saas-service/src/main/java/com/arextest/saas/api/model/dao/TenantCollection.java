package com.arextest.saas.api.model.dao;

import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author wildeslam.
 * @create 2024/3/4 19:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "Tenant")
@FieldNameConstants
public class TenantCollection extends ModelBase {

  private String email;
  private Set<String> providerUids;
  private String password;
  private String tenantCode;
  private String tenantName;
  private String phoneNumber;
  private String profile;
  private Integer userLevel;
  private Long packageEffectiveTime;
  private Long expireTime;
  private String tenantToken;
  private Set<UserInfo> userInfos;
  private Long trafficLimit;
  private Integer memberLimit;
  private String verificationCode;
  private Long verificationTime;

  @Data
  @FieldNameConstants
  @EqualsAndHashCode(onlyExplicitlyIncluded = true)
  public static class UserInfo {

    @EqualsAndHashCode.Include
    private String email;
    private Set<String> providerUids;
  }
}
