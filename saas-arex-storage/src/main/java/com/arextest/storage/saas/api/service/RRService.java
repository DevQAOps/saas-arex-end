package com.arextest.storage.saas.api.service;

import com.arextest.storage.saas.api.models.rr.RecordDto;
import com.arextest.storage.saas.api.models.rr.RecordListingReq;
import com.arextest.storage.saas.api.repository.rr.RREventsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author: QizhengMo
 * @date: 2024/11/12 11:29
 */
@Service
@RequiredArgsConstructor
public class RRService {
  private final RREventsRepository eventsRepository;

  public void record(RecordDto dto) {
    eventsRepository.record(dto);
  }

  public List<RecordDto> listRecords(RecordListingReq req) {
    return eventsRepository.list(req.getAppId(), req.getFrom(), req.getTo());
  }

  public RecordDto detail(String recordId) {
    return eventsRepository.viewRecord(recordId);
  }

  public List<RecordDto> search(RecordDto.Search search) {
    return eventsRepository.search(search);
  }
}
