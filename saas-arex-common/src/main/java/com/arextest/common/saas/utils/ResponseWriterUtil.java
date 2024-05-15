package com.arextest.common.saas.utils;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseStatusType;
import com.arextest.common.saas.enums.SaasErrorCode;
import com.arextest.common.utils.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class ResponseWriterUtil {

  private static final ObjectMapper mapper = new ObjectMapper();

  public static void setDefaultErrorResponse(@NonNull HttpServletResponse response,
      @NonNull HttpStatus httpStatus, SaasErrorCode errorCode) {
    setErrorResponse(response, httpStatus, "text/plain", "UTF-8", errorCode);
  }

  public static void setErrorResponse(@NonNull HttpServletResponse response,
      @NonNull HttpStatus httpStatus,
      @NonNull String contentType, @NonNull String characterEncoding, SaasErrorCode errorCode) {

    response.setStatus(httpStatus.value());
    response.setContentType(contentType);
    response.setCharacterEncoding(characterEncoding);

    if (errorCode != null) {
      ResponseStatusType responseStatusType = new ResponseStatusType();
      responseStatusType.setResponseCode(errorCode.getCodeValue());
      responseStatusType.setResponseDesc(errorCode.getMessage());
      responseStatusType.setTimestamp(System.currentTimeMillis());

      Response errorResponse = ResponseUtils.errorResponse(responseStatusType);
      try {
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
      } catch (Exception e) {
        LOGGER.error("Failed to write error response", e);
      }
    }
  }


}
