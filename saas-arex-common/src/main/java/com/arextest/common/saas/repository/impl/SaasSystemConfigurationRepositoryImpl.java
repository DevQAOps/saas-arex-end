package com.arextest.common.saas.repository.impl;

import com.arextest.common.saas.model.dao.SaasSystemConfigurationCollection;
import com.arextest.common.saas.model.dto.SaasSystemConfiguration;
import com.arextest.common.saas.model.mapper.SaasSystemConfigurationMapper;
import com.arextest.common.saas.repository.SaasSystemConfigurationRepository;
import com.arextest.config.model.dao.config.SystemConfigurationCollection;
import com.arextest.config.utils.MongoHelper;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SaasSystemConfigurationRepositoryImpl implements SaasSystemConfigurationRepository {

  private final MongoTemplate mongoTemplate;

  @Override
  public List<SaasSystemConfiguration> query(Collection<String> keys) {
    Query query = new Query();
    query.addCriteria(Criteria.where(SystemConfigurationCollection.Fields.key).in(keys));
    List<SaasSystemConfigurationCollection> daos = mongoTemplate.find(query,
        SaasSystemConfigurationCollection.class);
    return daos.stream().map(SaasSystemConfigurationMapper.INSTANCE::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public boolean save(SaasSystemConfiguration saasSystemConfiguration) {
    SaasSystemConfigurationCollection dao = SaasSystemConfigurationMapper.INSTANCE
        .dtoToEntity(saasSystemConfiguration);
    Query filter = new Query(Criteria.where(SystemConfigurationCollection.Fields.key)
        .is(saasSystemConfiguration.getKey()));
    Update update = MongoHelper.getFullTemplateUpdates(dao);
    MongoHelper.withMongoTemplateBaseUpdate(update);
    return mongoTemplate.upsert(filter, update, SystemConfigurationCollection.class)
        .getModifiedCount() > 0;
  }


}
