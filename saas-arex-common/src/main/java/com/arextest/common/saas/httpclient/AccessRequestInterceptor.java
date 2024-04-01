package com.arextest.common.saas.httpclient;

import com.arextest.common.jwt.JWTService;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Slf4j
@AllArgsConstructor
public class AccessRequestInterceptor implements ClientHttpRequestInterceptor {

  JWTService jwtService;

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    LOGGER.info("Request URI: {}", request.getURI());
    return execution.execute(request, body);
  }
}