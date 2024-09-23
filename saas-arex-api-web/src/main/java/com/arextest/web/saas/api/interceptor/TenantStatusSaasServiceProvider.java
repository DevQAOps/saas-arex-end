package com.arextest.web.saas.api.interceptor;

import com.arextest.common.saas.interceptor.TenantStatusProvider;
import com.arextest.common.saas.tenant.TenantStatusRedisInfo;
import com.arextest.web.core.business.beans.httpclient.HttpWebServiceApiClient;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TenantStatusSaasServiceProvider implements TenantStatusProvider {

  @Resource
  HttpWebServiceApiClient httpWebServiceApiClient;

  @Value("${saas.service.queryTenantStatus.url}")
  String queryTenantStatusUrl;

  @Override
  public TenantStatusRedisInfo fetchTenantStatus(String tenantCode) {
    QueryTenantStatusRequest request = new QueryTenantStatusRequest();
    request.setTenantCode(tenantCode);
    QueryTenantStatusResponse response = httpWebServiceApiClient.post(queryTenantStatusUrl, request,
        QueryTenantStatusResponse.class);
    return Optional.ofNullable(response).map(QueryTenantStatusResponse::getBody).orElse(null);
  }

  @Data
  private static class QueryTenantStatusRequest {

    private String tenantCode;
  }

  @Data
  private static class QueryTenantStatusResponse {

    TenantStatusRedisInfo body;
  }

}
