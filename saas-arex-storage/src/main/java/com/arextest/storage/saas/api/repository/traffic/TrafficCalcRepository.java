package com.arextest.storage.saas.api.repository.traffic;

import com.arextest.model.mock.MockCategoryType;
import com.arextest.storage.saas.api.models.traffic.CaseSummaryRequest;
import com.arextest.storage.saas.api.models.traffic.CaseSummaryRequest.Filter;
import com.arextest.storage.saas.api.models.traffic.CaseSummaryRequest.FilterType;
import com.arextest.storage.saas.api.models.traffic.TrafficAggregationResult;
import com.arextest.storage.saas.api.models.traffic.TrafficCase;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
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
  public Pair<Long, List<TrafficCase>> queryCaseBrief(CaseSummaryRequest req) {
    Set<MockCategoryType> entryPointTypes = Collections.singleton(req.getCategory());
    int limit = req.getPageSize();
    int skip = (req.getPageIndex() - 1) * limit;
    Query query = new Query(buildBaseQuery(req));
    query.with(Sort.by(TrafficCase.Fields.creationTime).descending());

    for (MockCategoryType entry : entryPointTypes) {
      String categoryName = entry.getName();
      long count = mongoTemplate.count(query, TrafficCase.class, getCollectionName(entry));
      if (count == 0) {
        continue;
      }

      List<TrafficCase> cases = mongoTemplate.find(query.limit(limit).skip(skip), TrafficCase.class, getCollectionName(entry));
      if (!CollectionUtils.isEmpty(cases)) {
        cases.forEach(c -> c.setType(categoryName));
        return Pair.of(count, cases);
      }
    }
    return Pair.of(0L, Collections.emptyList());
  }

  public List<TrafficAggregationResult> countCasesByRange(CaseSummaryRequest req, int step) {
    Criteria query = buildBaseQuery(req);
    MockCategoryType category = req.getCategory();
    // Match operation
    MatchOperation matchOperation = Aggregation.match(query);

    // Projection operation to create a new field
    ProjectionOperation projectionOperation = Aggregation.project()
        .and(
            ArithmeticOperators.Ceil.ceilValueOf(
                ArithmeticOperators.Divide.valueOf(
                    ConvertOperators.ToDecimal.toDecimal("$creationTime")
                ).divideBy(step)
            )
        ).as("creationTimeCeil");

    // Group operation
    GroupOperation groupOperation = Aggregation.group("creationTimeCeil")
        .count().as("count");

    // Create aggregation
    Aggregation aggregation = Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation);

    // Execute aggregation
    AggregationResults<TrafficAggregationResult> queryResult = mongoTemplate.aggregate(aggregation, getCollectionName(category), TrafficAggregationResult.class);
    return queryResult.getMappedResults();
  }

  private Criteria buildBaseQuery(CaseSummaryRequest req) {
    Date from = new Date(req.getBeginTime());
    Date to = new Date(req.getEndTime());
    String appId = req.getAppId();
    // base query
    Criteria criteria = Criteria
        .where(TrafficCase.Fields.creationTime).gte(from).lt(to)
        .and("appId").is(appId);

    // dynamic conditions
    if (!CollectionUtils.isEmpty(req.getFilters())) {
      List<Filter> filters = req.getFilters();
      List<String> endpoints = filters.stream()
          .filter(f -> f.getFilterType() == FilterType.ENDPOINT)
          .map(Filter::getValue)
          .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(endpoints)) {
        criteria = criteria.and(TrafficCase.Fields.operationName).in(endpoints);
      }
    }
    return criteria;
  }

  private String getCollectionName(MockCategoryType category) {
    return "Rolling" + category.getName() + "Mocker";
  }
}
