package com.arextest.saas.api.mails;

import com.arextest.saas.api.service.MailService;
import com.arextest.saas.api.common.utils.LoadResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import java.util.Base64;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2024/7/25
 */
@Slf4j
@Component
public class SendInvitationEmail {

  private static final String SEND_INVITATION_SUBJECT = "[AREX Cloud] Invitation";
  private static final String INVITATION_EMAIL_TEMPLATE = "classpath:invitationEmailTemplate.htm";
  private static final String INVITATION_LINK_FORMAT = "%s/client/verify?upn=%s";
  private static final String INVITATION_LINK_PLACEHOLDER = "{{invitationLink}}";

  @Value("${arex.verify.domain}")
  private String arexDomain;

  @Resource
  private ObjectMapper objectMapper;

  @Resource
  private MailService mailService;

  @Autowired
  private LoadResource loadResource;

  public boolean sendInvitationEmail(String email, String verificationCode) {
    Map<String, String> user = Maps.newHashMap();
    user.put("userName", email);
    user.put("verificationCode", verificationCode);

    String userJsonStr;
    try {
      userJsonStr = objectMapper.writeValueAsString(user);
    } catch (JsonProcessingException e) {
      LOGGER.error("Failed to send invitation email. email:{}", email, e);
      return false;
    }

    String invitationLink = String.format(INVITATION_LINK_FORMAT, arexDomain,
        Base64.getEncoder().encodeToString(userJsonStr.getBytes()));

    String template = loadResource.getResource(INVITATION_EMAIL_TEMPLATE);
    template = template.replace(INVITATION_LINK_PLACEHOLDER, invitationLink);
    try {
      return mailService.sendEmail(email, SEND_INVITATION_SUBJECT, template);
    } catch (Exception e) {
      LOGGER.error("Failed to send invitation email. email:{}", email, e);
      return false;
    }
  }
}
