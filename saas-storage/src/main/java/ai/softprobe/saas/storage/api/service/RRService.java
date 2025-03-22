package ai.softprobe.saas.storage.api.service;

import ai.softprobe.saas.storage.api.models.rr.RecordDto;
import ai.softprobe.saas.storage.api.models.rr.RecordListingReq;
import ai.softprobe.saas.storage.api.repository.rr.RREventsRepository;
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
