package ai.softprobe.saas.common.model.dto;

import ai.softprobe.saas.common.model.dao.SaasSystemConfigurationCollection.SubscribeInfo;
import com.arextest.config.model.dto.system.SystemConfiguration;
import lombok.Data;

@Data
public class SaasSystemConfiguration extends SystemConfiguration {

  private String tenantToken;
  private SubscribeInfo subscribeInfo;
}
