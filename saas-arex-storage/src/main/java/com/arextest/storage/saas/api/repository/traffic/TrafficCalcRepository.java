package com.arextest.storage.saas.api.repository.traffic;

import com.arextest.model.mock.MockCategoryType;
import com.arextest.storage.saas.api.models.traffic.TrafficCase;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

/**
 * @author: QizhengMo
 * @date: 2024/9/19 20:19
 */
@Repository
@RequiredArgsConstructor
public class TrafficCalcRepository {
  private final MongoTemplate mongoTemplate;
  public Pair<Long, List<TrafficCase>> queryCaseBrief(Set<MockCategoryType> entryPointTypes, String appId,
      Date from, Date to,
      int limit, int skip) {
    for (MockCategoryType entry : entryPointTypes) {
      String categoryName = entry.getName();

      Query query = new Query()
          .addCriteria(Criteria.where(TrafficCase.Fields.creationTime).gte(from).lt(to))
          .addCriteria(Criteria.where("appId").is(appId))
          .with(Sort.by(TrafficCase.Fields.creationTime).descending())
          .limit(limit)
          .skip(skip);

      long count = mongoTemplate.count(query, TrafficCase.class, getCollectionName(entry));
      if (count == 0) {
        continue;
      }

      List<TrafficCase> cases = mongoTemplate.find(query, TrafficCase.class, getCollectionName(entry));
      if (!CollectionUtils.isEmpty(cases)) {
        cases.forEach(c -> c.setType(categoryName));
        return Pair.of(count, cases);
      }
    }
    return Pair.of(0L, Collections.emptyList());
  }

  private String getCollectionName(MockCategoryType category) {
    return "Rolling" + category.getName() + "Mocker";
  }
}
