package com.arextest.common.saas.enums;

public enum TenantStatus {

  ACTIVE(0),
  INACTIVE(1);

  private int status;

  TenantStatus(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  public static TenantStatus fromStatus(int status) {
    for (TenantStatus tenantStatus : TenantStatus.values()) {
      if (tenantStatus.getStatus() == status) {
        return tenantStatus;
      }
    }
    return null;
  }

}
