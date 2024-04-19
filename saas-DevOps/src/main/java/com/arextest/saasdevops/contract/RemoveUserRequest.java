package com.arextest.saasdevops.contract;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author wildeslam.
 * @create 2024/3/29 15:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RemoveUserRequest extends BaseRequest {
    List<String> emails;
}
