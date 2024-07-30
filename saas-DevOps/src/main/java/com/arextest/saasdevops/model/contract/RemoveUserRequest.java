package com.arextest.saasdevops.model.contract;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wildeslam.
 * @create 2024/3/29 15:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RemoveUserRequest extends BaseRequest {

  List<String> emails;
}
