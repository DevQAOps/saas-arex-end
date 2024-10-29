package com.arextest.saas.api.configuration;

import com.arextest.saas.api.common.enums.PackageUnitType;
import jakarta.annotation.PostConstruct;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "arex.saas")
@Data
public class PackageConfiguration {

  List<PackageInfo> packageInfos;

  @PostConstruct
  public void init() {
    for (PackageInfo packageInfo : packageInfos) {
      PackageUnitType packageUnitType = PackageUnitType.fromValue(packageInfo.unit);
      if (packageUnitType != null) {
        packageInfo.setUnitType(packageUnitType);
      } else {
        throw new IllegalArgumentException("Unknown package unit type: " + packageInfo.unit);
      }
    }
  }

  public PackageInfo getPackageInfoById(Integer packageId) {
    for (PackageInfo packageInfo : packageInfos) {
      if (Objects.equals(packageInfo.packageId, packageId)) {
        return packageInfo;
      }
    }
    return null;
  }

  @Data
  public static class PackageInfo {

    private Integer packageId;
    private Integer num;
    private Integer unit;
    private PackageUnitType unitType;

    private Long trafficLimit;
    private Integer memberLimit;

    public Long calculateExpireTimestamp(Long startTimeStamp){
      Calendar expireTime = Calendar.getInstance();
      if (startTimeStamp == null) {
        expireTime.setTimeInMillis(System.currentTimeMillis());
      }else{
        expireTime.setTimeInMillis(startTimeStamp);
      }

      switch (unitType) {
        case DAY:
          expireTime.add(Calendar.DAY_OF_MONTH, num);
          break;
        case MONTH:
          expireTime.add(Calendar.MONTH, num);
          break;
        case YEAR:
          expireTime.add(Calendar.YEAR, num);
          break;
      }
      return floorExpireTime(expireTime).getTimeInMillis();
    }

    private Calendar floorExpireTime(Calendar calendar) {
      calendar.set(Calendar.HOUR_OF_DAY, 23);
      calendar.set(Calendar.MINUTE, 59);
      calendar.set(Calendar.SECOND, 59);
      calendar.set(Calendar.MILLISECOND, 999);
      return calendar;
    }

  }
}

