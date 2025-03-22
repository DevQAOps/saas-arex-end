package ai.softprobe.saas.api.mails;

import ai.softprobe.saas.api.service.MailService;
import ai.softprobe.saas.api.common.utils.LoadResource;
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
