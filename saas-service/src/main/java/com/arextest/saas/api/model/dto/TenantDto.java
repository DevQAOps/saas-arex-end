package com.arextest.saas.api.model.dto;

import com.arextest.saas.api.model.enums.TenantLevelEnum;
import java.util.Collection;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author wildeslam.
 * @create 2024/3/5 15:59
 */
@Data
@EqualsAndHashCode
public class TenantDto implements UserDetails {

  private String email;
  private Set<String> providerUids;
  private String password;
  private String tenantName;
  private String tenantCode;
  private String phoneNumber;
  private String profile;
  private Integer userLevel;
  private Long packageEffectiveTime;
  private Long expireTime;
  private String tenantToken;
  private Set<UserInfoDto> userInfos;
  private Long trafficLimit;
  private Integer memberLimit;
  private String verificationCode;
  private Long verificationTime;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return System.currentTimeMillis() < expireTime;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return TenantLevelEnum.DISABLED.getCode() != userLevel;
  }

  @Data
  @EqualsAndHashCode(onlyExplicitlyIncluded = true)
  public static class UserInfoDto {

    @EqualsAndHashCode.Include
    private String email;
    private Set<String> providerUids;
  }
}
