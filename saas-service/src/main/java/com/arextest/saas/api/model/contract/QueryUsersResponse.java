package com.arextest.saas.api.model.contract;

import java.util.Set;
import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/5/10 19:29
 */
@Data
public class QueryUsersResponse extends SuccessResponseType {

  private Set<String> userEmails;
}
