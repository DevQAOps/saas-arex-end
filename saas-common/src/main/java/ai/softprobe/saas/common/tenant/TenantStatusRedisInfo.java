package ai.softprobe.saas.common.tenant;

import lombok.Data;

@Data
public class TenantStatusRedisInfo {

  private String tenantToken;

  private Long expireTime;

}