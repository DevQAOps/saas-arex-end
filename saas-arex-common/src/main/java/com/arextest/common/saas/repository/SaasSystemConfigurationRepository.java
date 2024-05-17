package com.arextest.common.saas.repository;

import com.arextest.common.saas.model.dto.SaasSystemConfiguration;
import java.util.Collection;
import java.util.List;

public interface SaasSystemConfigurationRepository {

  List<SaasSystemConfiguration> query(Collection<String> keys);

  boolean save(SaasSystemConfiguration saasSystemConfiguration);
}
