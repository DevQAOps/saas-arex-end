package com.arextest.saasdevops.service;

import com.arextest.saasdevops.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wildeslam.
 * @create 2024/4/17 14:26
 */
@Service
public class UserManageServiceImpl implements UserManageService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean initSaasUser(String companyName, String email) {
        return false;
    }

    @Override
    public boolean addUser(String companyName, List<String> emails) {
        return userRepository.addUser(emails);
    }

    @Override
    public boolean removeUser(String companyName, List<String> emails) {
        return userRepository.removeUser(emails);
    }
}
