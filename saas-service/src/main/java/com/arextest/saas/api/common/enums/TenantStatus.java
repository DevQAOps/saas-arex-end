package com.arextest.saas.api.common.enums;

public enum TenantStatus {

  ACTIVE(0),
  INACTIVE(1);

  private int status;

  TenantStatus(int status) {
    this.status = status;
  }

  public static TenantStatus fromStatus(int status) {
    for (TenantStatus tenantStatus : TenantStatus.values()) {
      if (tenantStatus.getStatus() == status) {
        return tenantStatus;
      }
    }
    return null;
  }

  public int getStatus() {
    return status;
  }

}
