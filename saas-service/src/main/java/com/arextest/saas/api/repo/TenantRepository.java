package com.arextest.saas.api.repo;

import com.arextest.saas.api.model.dao.TenantCollection;
import com.arextest.saas.api.model.dto.TenantDto;
import java.util.List;

/**
 * @author wildeslam.
 * @create 2024/3/5 15:54
 */
public interface TenantRepository {

  TenantCollection upsertTenant(TenantDto tenantDto);

  TenantDto queryTenant(String tenantCode);

  TenantDto queryTenantByEmail(String email);

  Boolean deleteTenantInfo(String email);

  List<TenantCollection> queryTenantsByEmail(String email);

  List<TenantCollection> queryAllTenants();
}
