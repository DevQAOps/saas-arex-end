package com.arextest.saas.api.repo.impl;

import com.arextest.saas.api.common.utils.MongoHelper;
import com.arextest.saas.api.model.dao.TenantCollection;
import com.arextest.saas.api.model.dao.TenantCollection.UserInfo;
import com.arextest.saas.api.model.dto.TenantDto;
import com.arextest.saas.api.repo.TenantRepository;
import com.arextest.saas.api.repo.mapper.UserMapper;
import com.mongodb.client.result.DeleteResult;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * @author wildeslam.
 * @create 2024/3/5 15:54
 */
@Slf4j
@Repository
public class TenantRepositoryImpl implements TenantRepository {

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public TenantCollection upsertTenant(TenantDto tenantDto) {
    TenantCollection tenantCollection = UserMapper.INSTANCE.daoFromDto(tenantDto);
    Query query = Query.query(
        Criteria.where(TenantCollection.Fields.email).is(tenantDto.getEmail()));

    Update update = MongoHelper.builder().initUpdate()
        .appendFullProperties(tenantCollection).build().getUpdate();
    tenantCollection = mongoTemplate.findAndModify(query, update,
        FindAndModifyOptions.options().returnNew(true).upsert(true), TenantCollection.class);
    return tenantCollection;
  }

  @Override
  public TenantDto queryTenant(@NonNull String tenantCode) {
    Query query = Query.query(Criteria.where(TenantCollection.Fields.tenantCode).is(tenantCode));
    TenantCollection tenantCollection = mongoTemplate.findOne(query, TenantCollection.class);
    return UserMapper.INSTANCE.dtoFromDao(tenantCollection);
  }

  @Override
  public TenantDto queryTenantByEmail(String email) {
    Query query = Query.query(Criteria.where(TenantCollection.Fields.email).is(email));
    TenantCollection tenantCollection = mongoTemplate.findOne(query, TenantCollection.class);
    return UserMapper.INSTANCE.dtoFromDao(tenantCollection);
  }

  @Override
  public Boolean deleteTenantInfo(String email) {
    Query query = Query.query(Criteria.where(TenantCollection.Fields.email).is(email));
    DeleteResult result = mongoTemplate.remove(query, TenantCollection.class);
    return result.getDeletedCount() > 0;
  }

  @Override
  public List<TenantCollection> queryTenantsByEmail(String email) {
    Query query = new Query(
        Criteria.where(toDot(TenantCollection.Fields.userInfos, UserInfo.Fields.email))
            .is(email)
    );
    return mongoTemplate.find(query, TenantCollection.class);
  }

  @Override
  public List<TenantCollection> queryAllTenants() {
    return mongoTemplate.find(new Query(Criteria.where(TenantCollection.Fields.tenantCode).ne(null)), TenantCollection.class);
  }

  private String toDot(String... fieldNames) {
    return String.join(".", fieldNames);
  }
}
