package com.arextest.saas.api.configuration;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.common.utils.ResponseUtils_New;
import com.arextest.saas.api.common.exceptions.ArexSaasException;
import com.arextest.web.common.LogUtils;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author b_yu
 * @since 2022/11/18
 */
@Slf4j
@RestControllerAdvice
public class ControllerException {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Response handleValidException(MethodArgumentNotValidException e) {
    List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
    String message = allErrors.stream().map(s -> s.getDefaultMessage())
        .collect(Collectors.joining(";"));
    return ResponseUtils.errorResponse(message, ResponseCode.REQUESTED_PARAMETER_INVALID);
  }

  @ExceptionHandler(Throwable.class)
  public Response handleDefaultException(Throwable e) {
    LogUtils.error(LOGGER, "Unhandled exception", e);
    return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
  }

  @ExceptionHandler(ArexSaasException.class)
  public Response handleArexException(ArexSaasException e) {
    LogUtils.warn(LOGGER, "Arex internal exception", e);
    return ResponseUtils_New.errorResponse(e.getMessage(), e.getCode());
  }
}