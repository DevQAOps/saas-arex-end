package ai.softprobe.saas.storage.api.models.rr;

import com.arextest.common.utils.SerializationUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

/**
 * @author: QizhengMo
 * @date: 2024/11/12 14:08
 */
@Mapper(componentModel = "spring")
public interface Converter {

  @Mappings(
      @Mapping(target = "data", source = "data", qualifiedByName = "eventCompress")
  )
  RecordDocument.Event eventDtoToDoc(RecordDto.EventDto req);

  @Mappings(
      @Mapping(target = "data", source = "data", qualifiedByName = "eventDecompress")
  )
  RecordDto.EventDto eventDocToDto(RecordDocument.Event event);

  @Named("eventCompress")
  default String eventCompress(Object data) {
    return SerializationUtils.useZstdSerializeToBase64(data);
  }

  @Named("eventDecompress")
  default Object eventDecompress(String data) {
    return SerializationUtils.useZstdDeserialize(data, Object.class);
  }


  RecordDto recordDocumentToDto(RecordDocument recordDocument);
}
