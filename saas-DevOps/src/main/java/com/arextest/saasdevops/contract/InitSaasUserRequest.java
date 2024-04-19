package com.arextest.saasdevops.contract;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wildeslam.
 * @create 2024/3/28 19:43
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InitSaasUserRequest extends BaseRequest {
    private String email;
}
