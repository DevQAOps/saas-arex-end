package ai.softprobe.saas.api.repo.mapper;

import ai.softprobe.saas.api.model.contract.QueryUsageRequest;
import ai.softprobe.saas.api.model.contract.external.QueryTenantUsageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author b_yu
 * @since 2024/7/24
 */
@Mapper
public interface QueryUsageMapper {

  QueryUsageMapper INSTANCE = Mappers.getMapper(QueryUsageMapper.class);

  QueryTenantUsageRequest toQueryTenantUsageRequest(QueryUsageRequest request);

}
