package com.arextest.saasdevops.repository;

import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.UserCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wildeslam.
 * @create 2024/4/17 11:20
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public boolean addUser(List<String> emails) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, UserCollection.class);
        for (String email : emails) {
            Query query = Query.query(Criteria.where(UserCollection.Fields.userName).is(email));
            Update update = MongoHelper.getUpdate();
            update.set(UserCollection.Fields.userName, email);
            bulkOps.upsert(query, update);
        }
        bulkOps.execute();
        return true;
    }

    @Override
    public boolean removeUser(List<String> emails) {
        Query query = Query.query(Criteria.where(UserCollection.Fields.userName).in(emails));
        mongoTemplate.remove(query, UserCollection.class);
        return true;
    }
}
