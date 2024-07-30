package com.arextest.common.saas.multitenant.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: QizhengMo
 * @date: 2024/3/29 10:47
 */
@Getter
@Setter
@Builder
@ToString
public class TenantMongoClientHolder {

  private MongoClient mongoClient;
  private MongoDatabase mongoDatabase;
  private String databaseName;
}
