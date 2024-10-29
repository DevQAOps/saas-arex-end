package com.arextest.saas.api.common.utils;

import com.arextest.saas.api.common.constants.Constants;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @author wildeslam.
 * @create 2024/3/6 11:26
 */
@Slf4j
@Data
@Builder
public class MongoHelper {

  private Update update;

  private static Map<String, Field> getAllField(Object bean) {
    Class<?> clazz = bean.getClass();
    Map<String, Field> fieldMap = new HashMap<>();
    while (clazz != null) {
      for (Field field : clazz.getDeclaredFields()) {
        // ignore static and synthetic field such as $jacocoData
        if (field.isSynthetic()) {
          continue;
        }
        if (!fieldMap.containsKey(field.getName())) {
          fieldMap.put(field.getName(), field);
        }
      }
      clazz = clazz.getSuperclass();
    }
    return fieldMap;
  }

  public static class MongoHelperBuilder {

    public MongoHelperBuilder() {
      this.update = new Update();
    }

    public MongoHelperBuilder initUpdate() {
      this.update.set(Constants.DATA_CHANGE_UPDATE_TIME, System.currentTimeMillis());
      this.update.setOnInsert(Constants.DATA_CHANGE_CREATE_TIME, System.currentTimeMillis());
      return this;
    }

    public MongoHelperBuilder appendFullProperties(Object obj) {
      Map<String, Field> allFields = getAllField(obj);
      for (Field field : allFields.values()) {
        try {
          field.setAccessible(true);
          if (field.get(obj) != null) {
            this.update.set(field.getName(), field.get(obj));
          }
        } catch (IllegalAccessException e) {
          LOGGER.error(String.format("Class:[%s]. failed to get field %s", obj.getClass().getName(),
              field.getName()), e);
        }
      }
      return this;
    }

    public MongoHelperBuilder appendSpecifiedProperties(Object obj, String... fieldNames) {
      Map<String, Field> allField = getAllField(obj);
      for (String fieldName : fieldNames) {
        try {
          if (allField.containsKey(fieldName)) {
            Field declaredField = allField.get(fieldName);
            declaredField.setAccessible(true);
            Object targetObj = declaredField.get(obj);
            if (targetObj != null) {
              this.update.set(fieldName, targetObj);
            }
          }
        } catch (IllegalAccessException e) {
          LOGGER.error(String.format("Class:[%s]. failed to get field %s", obj.getClass().getName(),
              fieldName), e);
        }
      }
      return this;
    }
  }
}