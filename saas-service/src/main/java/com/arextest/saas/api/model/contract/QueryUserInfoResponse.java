package com.arextest.saas.api.model.contract;

import com.arextest.saas.api.model.vo.TenantVo;
import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/4/2 19:55
 */
@Data
public class QueryUserInfoResponse extends SuccessResponseType {

  private TenantVo user;
}
