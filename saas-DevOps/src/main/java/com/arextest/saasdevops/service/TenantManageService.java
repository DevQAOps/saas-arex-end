package com.arextest.saasdevops.service;

import com.arextest.saasdevops.model.dto.TenantStatusInfo;

public interface TenantManageService {

  boolean initTenantStatus(TenantStatusInfo request);

}
