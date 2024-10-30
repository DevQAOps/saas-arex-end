package com.arextest.saas.api.model.contract;

import com.arextest.saas.api.model.vo.TenantVo;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ClientOauthResponse {

  private List<TenantVo> tenantInfos;
  private String authToken;

  private Map<String, String> providerProfile;
}
