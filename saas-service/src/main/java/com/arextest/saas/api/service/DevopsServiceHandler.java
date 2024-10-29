package com.arextest.saas.api.service;

import com.arextest.common.model.response.GenericResponseType;
import com.arextest.saas.api.service.http.HttpWebClient;
import com.arextest.saas.api.model.contract.external.FinalizeSaasUserRequest;
import com.arextest.saas.api.model.contract.external.InitSaasUserRequest;
import com.arextest.saas.api.model.contract.external.QueryTenantUsageRequest;
import jakarta.annotation.Resource;
import java.util.Map;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DevopsServiceHandler {

  private static final String TOTAL_BYTES = "totalBytes";
  private static final String SUCCESS = "success";
  @Value("${arex.devops.url}")
  private String devopsUrl;
  @Value("${devops.url.init}")
  private String initTenantUrl;
  @Value("${devops.url.finalize}")
  private String finalizeTenantUrl;
  @Value("${devops.url.usage.query}")
  private String queryUsageUrl;
  @Value("${devops.url.usage.update.subscribe}")
  private String updateSubscribeUrl;
  @Resource
  private HttpWebClient httpWebClient;

  public boolean initialUserRepo(InitSaasUserRequest request) {
    String requestUrl = devopsUrl + initTenantUrl;
    GenericResponseType response = httpWebClient.jsonPost(requestUrl, request,
        GenericResponseType.class);
    if (response == null || response.getResponseStatusType().hasError()
        || response.getBody() == null) {
      throw new RuntimeException("Failed to init saas tenant");
    }
    return true;
  }

  public boolean finalizeUserRepo(FinalizeSaasUserRequest request) {
    String requestUrl = devopsUrl + finalizeTenantUrl;
    GenericResponseType response = httpWebClient.jsonPost(requestUrl, request,
        GenericResponseType.class);
    if (response == null || response.getResponseStatusType().hasError()
        || response.getBody() == null) {
      throw new RuntimeException("Failed to finalize saas tenant");
    }
    return true;
  }

  public Long queryTrafficUsage(QueryTenantUsageRequest request) {
    String requestUrl = devopsUrl + queryUsageUrl;
    GenericResponseType response = httpWebClient.jsonPost(requestUrl, request,
        GenericResponseType.class);
    if (response == null || response.getResponseStatusType().hasError()
        || response.getBody() == null) {
      throw new RuntimeException("Failed to query usage");
    }
    Map<String, Object> body = (Map<String, Object>) response.getBody();

    return Long.valueOf(body.get(TOTAL_BYTES).toString());
  }


  public boolean updateSubscribe(String tenantCode, Long trafficLimit, Long start, Long end) {
    String requestUrl = devopsUrl + updateSubscribeUrl;
    UpdateSubscribeRequest updateSubscribeRequest = new UpdateSubscribeRequest();
    updateSubscribeRequest.setTenantCode(tenantCode);
    updateSubscribeRequest.setTrafficLimit(trafficLimit);
    updateSubscribeRequest.setStart(start);
    updateSubscribeRequest.setEnd(end);

    GenericResponseType response = httpWebClient.jsonPost(requestUrl, updateSubscribeRequest,
        GenericResponseType.class);

    if (response == null || response.getResponseStatusType().hasError()
        || response.getBody() == null) {
      throw new RuntimeException("Failed to update usage limit");
    }
    Map<String, Object> body = (Map<String, Object>) response.getBody();

    return Boolean.parseBoolean(body.get(SUCCESS).toString());
  }

  @Data
  static class UpdateSubscribeRequest {

    private String tenantCode;
    private Long trafficLimit;
    private Long start;
    private Long end;
  }
}
