package com.arextest.web.saas.api.service;

import com.arextest.common.saas.model.SaasSystemConfigurationKeySummary;
import com.arextest.common.saas.model.dao.SaasSystemConfigurationCollection.Fields;
import com.arextest.common.saas.model.dto.SaasSystemConfiguration;
import com.arextest.common.saas.repository.SaasSystemConfigurationRepository;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Resource;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SaasSystemConfigurationService {

  @Resource
  SystemConfigurationRule systemConfigurationRule;

  @Resource
  SaasSystemConfigurationRepository saasSystemConfigurationRepository;


  public Map<String, Object> queryConfig() {
    Map<String, Object> res = new HashMap<>();
    Map<String, SystemConfigRule> mapping = systemConfigurationRule.getMapping();
    Set<String> keySet = mapping.keySet();
    List<SaasSystemConfiguration> configurationList = saasSystemConfigurationRepository.query(
        keySet);

    for (SaasSystemConfiguration item : configurationList) {
      SystemConfigRule rule = mapping.get(item.getKey());
      if (Objects.equals(rule.getKey(), SaasSystemConfigurationKeySummary.SAAS_TENANT_TOKEN)) {
        Object fieldValue = getFieldValue(item, rule.getFieldName());
        res.put(rule.getKey(), fieldValue);
      }
    }
    return res;
  }

  public Object getFieldValue(Object object, String fieldName) {
    try {
      Class<?> clazz = object.getClass();
      Field field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(object);
    } catch (Exception e) {
      LOGGER.error("getFieldValue error, fieldName:{}, exception:{}", fieldName, e.getMessage());
    }
    return null;
  }

  @Component
  public static class SystemConfigurationRule {

    public Map<String, SystemConfigRule> getMapping() {
      SystemConfigRule rule1 = SystemConfigRule.builder()
          .key(SaasSystemConfigurationKeySummary.SAAS_TENANT_TOKEN)
          .fieldName(Fields.tenantToken).build();

      SystemConfigRule rule2 = SystemConfigRule.builder()
          .key(SaasSystemConfigurationKeySummary.SAAS_SUBSCRIBE_INFO)
          .fieldName(Fields.subscribeInfo).build();

      return ImmutableMap.of(rule1.getKey(), rule1, rule2.getKey(), rule2);
    }
  }

  @Data
  @Builder
  public static class SystemConfigRule {

    private String key;
    private String fieldName;
    private boolean allowUpdate = true;
  }

}
