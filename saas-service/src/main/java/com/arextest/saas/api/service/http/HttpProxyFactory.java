package com.arextest.saas.api.service.http;

import java.net.InetSocketAddress;
import java.net.Proxy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HttpProxyFactory {

  @Value("${http.proxyHost:}")
  private String httpProxyHost;

  @Value("${http.proxyPort:}")
  private String httpProxyPort;

  public Proxy createProxy() {
    if (StringUtils.isNotEmpty(httpProxyHost) && StringUtils.isNotEmpty(httpProxyPort)) {
      return new Proxy(Proxy.Type.HTTP,
          new InetSocketAddress(httpProxyHost, Integer.parseInt(httpProxyPort)));
    }
    return Proxy.NO_PROXY;
  }

}
