package com.arextest.storage.saas.api.repository.rr;

import com.arextest.storage.saas.api.models.rr.Converter;
import com.arextest.storage.saas.api.models.rr.RecordDocument;
import com.arextest.storage.saas.api.models.rr.RecordDocument.Fields;
import com.arextest.storage.saas.api.models.rr.RecordDto;
import com.arextest.storage.saas.api.models.rr.RecordDto.EventDto;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * @author: QizhengMo
 * @date: 2024/11/12 11:32
 */
@Repository
@RequiredArgsConstructor
public class RREventsRepository {
  private final MongoTemplate mongoTemplate;
  private final Converter converter;

  public void record(String appId, String recordId, List<EventDto> events) {
    Query q = new Query(Criteria.where(Fields.recordId).is(recordId));

    Update update = new Update()
        .setOnInsert(Fields.appId, appId)
        .setOnInsert(Fields.createTime, new Date())
        .set(Fields.updateTime, new Date())
        .push(Fields.events).each(events.stream().map(converter::eventDtoToDoc).toArray());
    mongoTemplate.upsert(q, update, RecordDocument.class);
  }

  public List<RecordDto> list(String appId, Date from, Date to) {

    Query q = new Query(Criteria.where(Fields.appId).is(appId)
        .and(Fields.createTime).gte(from).lt(to));
    q.fields().exclude(Fields.events);
    return mongoTemplate.find(q, RecordDocument.class).stream()
        .map(converter::recordDocumentToDto)
        .toList();
  }

  public RecordDto viewRecord(String recordId) {
    Query q = new Query(Criteria.where(Fields.recordId).is(recordId));
    return Optional.ofNullable(mongoTemplate.findOne(q, RecordDocument.class))
        .map(converter::recordDocumentToDto)
        .orElse(null);
  }
}
