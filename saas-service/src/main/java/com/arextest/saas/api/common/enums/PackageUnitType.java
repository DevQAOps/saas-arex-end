package com.arextest.saas.api.common.enums;

public enum PackageUnitType {

  DAY(0),
  MONTH(1),
  YEAR(2);

  int value;

  PackageUnitType(int value) {
    this.value = value;
  }

  public static PackageUnitType fromValue(int value) {
    for (PackageUnitType packageUnitType : PackageUnitType.values()) {
      if (packageUnitType.value == value) {
        return packageUnitType;
      }
    }
    return null;
  }

}