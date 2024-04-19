package com.arextest.saasdevops.repository;

import java.util.List;

/**
 * @author wildeslam.
 * @create 2024/4/7 17:32
 */
public interface UserRepository {
    /**
     * Add users to the organization.
     * return true if the addition is successful.
     */
    boolean addUser(List<String> emails);

    /**
     * Remove users to the organization.
     * return true if the removal is successful.
     */
    boolean removeUser(List<String> emails);
}
