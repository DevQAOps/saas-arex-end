package com.arextest.saas.api.mails;

import com.arextest.saas.api.service.MailService;
import com.arextest.saas.api.service.VerificationHandler;
import com.arextest.saas.api.common.utils.LoadResource;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2024/7/25
 */
@Component
public class SendVerificationEmail extends AbstractSendEmail {

  @Autowired
  public SendVerificationEmail(LoadResource loadResource, MailService mailService) {
    this.mailTitle = "[AREX Cloud] Verification Link";
    this.mailTemplate = "classpath:verificationCodeEmailTemplate.htm";
    this.loadResource = loadResource;
    this.mailService = mailService;
  }

  private static final String VERIFICATION_LINK_PLACEHOLDER = "{{verificationLink}}";
  private static final String VERIFICATION_LINK_FORMAT = "%s/api/signup/bind?upn=%s";

  @Value("${arex.verify.domain}")
  private String arexVerifyDomain;

  @Resource
  private VerificationHandler verificationHandler;

  public boolean sendVerificationEmail(String email, String verificationCode) {
    String verificationLink = String.format(VERIFICATION_LINK_FORMAT, arexVerifyDomain,
        verificationHandler.generateToken(email, verificationCode));

    String template = loadResource.getResource(mailTemplate);
    String body =  template.replace(VERIFICATION_LINK_PLACEHOLDER, verificationLink);
    return mailService.sendEmail(email, mailTitle, body);
  }
}
