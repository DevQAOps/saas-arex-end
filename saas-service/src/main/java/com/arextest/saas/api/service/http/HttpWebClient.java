package com.arextest.saas.api.service.http;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author wildeslam.
 * @create 2024/3/5 14:26
 */
@Slf4j
@Component
public class HttpWebClient {

  private RestTemplate restTemplate;

  @Autowired
  private HttpProxyFactory httpProxyFactory;

  @PostConstruct
  private void initRestTemplate() {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setProxy(httpProxyFactory.createProxy());
    restTemplate = new RestTemplate(requestFactory);
  }


  public <TResponse> TResponse get(String url, Map<String, ?> urlVariables,
      Class<TResponse> responseType) {
    try {
      return restTemplate.getForObject(url, responseType, urlVariables);
    } catch (Exception e) {
      return null;
    }
  }

  public <TRequest, TResponse> TResponse jsonPost(String url, TRequest request,
      Class<TResponse> responseType) {
    try {
      return restTemplate.postForObject(url, wrapJsonContentType(request), responseType);
    } catch (Exception e) {
      LOGGER.error("http post failed, url:{}", url, e);
      return null;
    }
  }

  private <TRequest> HttpEntity<TRequest> wrapJsonContentType(TRequest request) {
    HttpEntity<TRequest> httpJsonEntity;
    if (request instanceof HttpEntity) {
      httpJsonEntity = (HttpEntity<TRequest>) request;
    } else {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      httpJsonEntity = new HttpEntity<>(request, headers);
    }
    return httpJsonEntity;
  }

}
