package com.arextest.common.saas.httpclient;

import com.arextest.common.jwt.JWTService;
import com.arextest.common.saas.model.Constants;
import com.arextest.common.utils.GroupContextUtil;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Slf4j
@AllArgsConstructor
public class AccessRequestInterceptor implements ClientHttpRequestInterceptor {

  private final long TEMPORARY_EXPIRATION_MS = 20;

  private final String TOKEN_KEY_FIELD = "access-token";

  JWTService jwtService;

  /**
   * http://inner-service-address
   */
  Set<String> innerServiceAddress;

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    URI uri = request.getURI();
    String webSite =
        Optional.ofNullable(uri.getScheme()).orElse(Strings.EMPTY)
            + "://"
            + Optional.ofNullable(uri.getAuthority()).orElse(Strings.EMPTY);
    if (innerServiceAddress.contains(webSite)) {

      // todo temporary plan, to solve the problem of starting to read the system configuration
      //  and there is no group
      if (GroupContextUtil.getGroup() == null) {
        GroupContextUtil.setGroup(Strings.EMPTY);
      }

      // The service currently does not need to pass userName upstream and downstream, so it is left blank.
      // If you want to pass userName later, you can pass threadlocal
      String temporaryToken = jwtService.makeAccessToken("", TEMPORARY_EXPIRATION_MS);
      request.getHeaders().add(TOKEN_KEY_FIELD, temporaryToken);
      request.getHeaders().add(Constants.ORG, GroupContextUtil.getGroup());
    }
    return execution.execute(request, body);
  }
}