package com.arextest.saas.api.service.impl;

import com.arextest.saas.api.service.http.HttpWebClient;
import com.arextest.saas.api.service.MailService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author b_yu
 * @since 2024/7/29
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {

  private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

  private String sendEmailUrl;

  @Value("${arex.email.domain}")
  private String arexEmailDomain;

  @Resource
  private HttpWebClient httpWebClient;

  public boolean sendEmail(String mailBox, String subject, String htmlMsg) {
    if (StringUtils.isEmpty(sendEmailUrl)) {
      sendEmailUrl = arexEmailDomain + "/email/sendEmail";
    }
    SendMailRequest request = new SendMailRequest(mailBox, subject, htmlMsg, CONTENT_TYPE);
    SendMailResponse response = httpWebClient.jsonPost(sendEmailUrl, request, SendMailResponse.class);
    if (response == null || response.getData() == null || !response.getData().getSuccess()) {
      LOGGER.error("Failed to send email. mailBox:" + mailBox);
      return false;
    }
    return true;
  }

  @Data
  @AllArgsConstructor
  public static class SendMailRequest {

    private String to;
    private String subject;
    private String body;
    private String contentType;
  }

  @Data
  public static class SendMailResponse {

    private Body data;
  }

  @Data
  public static class Body {

    private Boolean success;
  }
}
