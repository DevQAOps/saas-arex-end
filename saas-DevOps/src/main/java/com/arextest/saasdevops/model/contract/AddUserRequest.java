package com.arextest.saasdevops.model.contract;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author wildeslam.
 * @create 2024/3/29 14:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddUserRequest extends BaseRequest {
    /**
     * Invited users' emails.
     */
    List<UserType> emails;
}
