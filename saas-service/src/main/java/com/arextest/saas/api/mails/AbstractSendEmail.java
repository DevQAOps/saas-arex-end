package com.arextest.saas.api.mails;

import com.arextest.saas.api.service.MailService;
import com.arextest.saas.api.common.utils.LoadResource;
import lombok.extern.slf4j.Slf4j;

/**
 * @author b_yu
 * @since 2024/8/8
 */
@Slf4j
public abstract class AbstractSendEmail {

  protected LoadResource loadResource;
  protected MailService mailService;

  protected String mailTitle;
  protected String mailTemplate;

}
