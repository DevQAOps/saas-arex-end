package ai.softprobe.saas.devops.repository;

import ai.softprobe.saas.devops.model.contract.UserType;

import java.util.List;

/**
 * @author wildeslam.
 * @create 2024/4/7 17:32
 */
public interface UserRepository {

  /**
   * Add users to the organization. return true if the addition is successful.
   */
  boolean addUser(List<UserType> emails);

  /**
   * Remove users to the organization. return true if the removal is successful.
   */
  boolean removeUser(List<String> emails);
}
