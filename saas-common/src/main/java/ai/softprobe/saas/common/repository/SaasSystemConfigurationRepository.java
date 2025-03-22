package ai.softprobe.saas.common.repository;

import ai.softprobe.saas.common.model.dto.SaasSystemConfiguration;
import java.util.Collection;
import java.util.List;

public interface SaasSystemConfigurationRepository {

  List<SaasSystemConfiguration> query(Collection<String> keys);

  boolean save(SaasSystemConfiguration saasSystemConfiguration);
}
