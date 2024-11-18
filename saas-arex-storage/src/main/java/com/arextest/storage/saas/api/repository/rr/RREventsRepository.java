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
import org.springframework.data.domain.Sort;
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

  public void record(RecordDto dto) {
    Query q = new Query(Criteria.where(Fields.recordId).is(dto.getRecordId()));

    Update update = new Update()
        .setOnInsert(Fields.appId, dto.getAppId())
        .set(Fields.userId, dto.getUserId())
        .set(Fields.clientId, dto.getClientId())
        .set(Fields.mobileNo, dto.getMobileNo())
        .set(Fields.ext, dto.getExt())

        .setOnInsert(Fields.createTime, new Date())
        .set(Fields.updateTime, new Date())
        .push(Fields.events).each(dto.getEvents().stream().map(converter::eventDtoToDoc).toArray());
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

  public List<RecordDto> search(RecordDto.Search search) {
    Query q = new Query();
    if (search.getRecordId() != null) {
      q.addCriteria(Criteria.where(Fields.recordId).is(search.getRecordId()));
    }
    if (search.getAppId() != null) {
      q.addCriteria(Criteria.where(Fields.appId).is(search.getAppId()));
    }
    if (search.getUserId() != null) {
      q.addCriteria(Criteria.where(Fields.userId).is(search.getUserId()));
    }
    if (search.getClientId() != null) {
      q.addCriteria(Criteria.where(Fields.clientId).is(search.getClientId()));
    }
    if (search.getMobileNo() != null) {
      q.addCriteria(Criteria.where(Fields.mobileNo).is(search.getMobileNo()));
    }
    if (search.getExt() != null) {
      search.getExt().forEach((k, v) -> q.addCriteria(Criteria.where(Fields.ext + "." + k).is(v)));
    }
    if (search.getCreateTimeFrom() != null) {
      q.addCriteria(Criteria.where(Fields.createTime).gte(search.getCreateTimeFrom()));
    }
    if (search.getCreateTimeTo() != null) {
      q.addCriteria(Criteria.where(Fields.createTime).lt(search.getCreateTimeTo()));
    }

    q.with(Sort.by(Sort.Order.desc(Fields.createTime)));
    q.limit(100);
    q.fields().exclude(Fields.events);

    return mongoTemplate.find(q, RecordDocument.class).stream()
        .map(converter::recordDocumentToDto)
        .toList();
  }
}
