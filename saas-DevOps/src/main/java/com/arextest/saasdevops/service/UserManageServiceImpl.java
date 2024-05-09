package com.arextest.saasdevops.service;

import com.arextest.config.model.dao.config.SystemConfigurationCollection;
import com.arextest.saasdevops.repository.UserRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author wildeslam.
 * @create 2024/4/17 14:26
 */
@Service
public class UserManageServiceImpl implements UserManageService {

    private static final String COMPANY_DATABASE_FORMAT = "%s_arex_storage_db";

    private static final String MONGO_DATABASE_PASSWORD = "iLoveArex";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoClient mongoClient;

    @Override
    public boolean initSaasUser(String tenantCode, String email) {
        Long currentTime = System.currentTimeMillis();

        // create company database and user
        String databaseName = String.format(COMPANY_DATABASE_FORMAT, tenantCode);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        BasicDBObject createUserCommand = new BasicDBObject("createUser", "arex")
            .append("pwd", MONGO_DATABASE_PASSWORD)
            .append("roles", Collections.singletonList(new BasicDBObject("role", "readWrite")
                .append("db", databaseName)));


        mongoDatabase.runCommand(createUserCommand);

        MongoCollection<SystemConfigurationCollection> systemConfigurationCollection =
            mongoDatabase.getCollection("SystemConfiguration", SystemConfigurationCollection.class);

        SystemConfigurationCollection systemConfiguration = new SystemConfigurationCollection();
        systemConfiguration.setJwtSeed(generateRandomCode(tenantCode));
        systemConfiguration.setKey(SystemConfigurationCollection.KeySummary.JWT_SEED);
        systemConfiguration.setDataChangeCreateTime(currentTime);
        systemConfiguration.setDataChangeUpdateTime(currentTime);
        systemConfigurationCollection.insertOne(systemConfiguration);


        MongoCollection<com.arextest.web.model.dao.mongodb.UserCollection> userCollection =
            mongoDatabase.getCollection("User", com.arextest.web.model.dao.mongodb.UserCollection.class);
        com.arextest.web.model.dao.mongodb.UserCollection user = new com.arextest.web.model.dao.mongodb.UserCollection();
        user.setUserName(email);
        user.setDataChangeCreateTime(currentTime);
        user.setDataChangeUpdateTime(currentTime);
        userCollection.insertOne(user);
        return true;
    }

    @Override
    public boolean addUser(String tenantCode, List<String> emails) {
        return userRepository.addUser(emails);
    }

    @Override
    public boolean removeUser(String tenantCode, List<String> emails) {
        return userRepository.removeUser(emails);
    }

    private String generateRandomCode(String tenantCode) {
        return UUID.nameUUIDFromBytes((tenantCode + System.currentTimeMillis()).getBytes()).toString();
    }
}
