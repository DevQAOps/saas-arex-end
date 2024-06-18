package com.arextest.saasdevops.config;

import java.sql.Timestamp;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;

/**
 * @author wildeslam.
 * @create 2024/6/18 15:10
 */
public class TimestampConverter implements Converter<Date, Timestamp> {

  @Override
  public Timestamp convert(Date date) {
    return new Timestamp(date.getTime());
  }
}
