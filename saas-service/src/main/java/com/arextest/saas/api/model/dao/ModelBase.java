package com.arextest.saas.api.model.dao;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

/**
 * @author wildeslam.
 * @create 2024/3/4 19:47
 */
@Data
@FieldNameConstants
public class ModelBase {

  @BsonProperty("_id")
  @BsonId
  private ObjectId id;
  private Long dataChangeCreateTime;
  private Long dataChangeUpdateTime;
}
