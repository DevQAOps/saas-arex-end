package com.arextest.common.saas.interceptor;

import com.arextest.common.saas.tenant.TenantStatusRedisInfo;

public interface TenantStatusProvider {

    TenantStatusRedisInfo fetchTenantStatus(String tenantCode);

}
