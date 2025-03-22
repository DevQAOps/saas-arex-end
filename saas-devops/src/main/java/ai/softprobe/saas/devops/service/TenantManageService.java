package ai.softprobe.saas.devops.service;

import ai.softprobe.saas.devops.model.dto.TenantStatusInfo;

public interface TenantManageService {

  boolean initTenantStatus(TenantStatusInfo request);

}
