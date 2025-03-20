package com.overflow.laundry.controller.exception;

import com.overflow.laundry.config.StandardResponse;
import com.overflow.laundry.constant.ObjectValidatorErrors;
import com.overflow.laundry.exception.MachineIdentifierAlreadyInUseException;
import com.overflow.laundry.exception.MachineNotFoundException;
import com.overflow.laundry.exception.StandardErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static com.overflow.laundry.constant.ObjectValidatorErrors.MessageResponseEnum.BAD_REQUEST;
import static com.overflow.laundry.constant.ObjectValidatorErrors.MessageResponseEnum.MACHINE_NOT_FOUND;


@RestControllerAdvice
public class ControllerExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);
  private static final String LOG_PREFIX = "[LAUNDRY-APP]";


  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<StandardResponse<StandardErrorMessage>> handleAllExceptions(
      Exception ex, WebRequest request) {
    StandardErrorMessage message = getStandardErrorMessage(ex, request);
    return StandardResponse.error(ObjectValidatorErrors.MessageResponseEnum.INTERNAL_SERVER_ERROR, message);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<StandardResponse<StandardErrorMessage>> handleNoResourceFoundException(
      NoResourceFoundException ex, WebRequest request) {
    StandardErrorMessage message = getStandardErrorMessage(ex, request);
    return StandardResponse.error(ObjectValidatorErrors.MessageResponseEnum.NOT_FOUND, message);
  }


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<StandardResponse<StandardErrorMessage>> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {

    StandardErrorMessage errorMessage = StandardErrorMessage.builder()
        .details(ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();
    return StandardResponse.error(BAD_REQUEST, errorMessage);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<StandardResponse<StandardErrorMessage>> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    StandardErrorMessage message = StandardErrorMessage.builder()
        .details(ex.getName() + " should be of type " + ex.getRequiredType().getName())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();
    return StandardResponse.error(BAD_REQUEST, message);
  }

  @ExceptionHandler(MachineNotFoundException.class)
  public ResponseEntity<StandardResponse<StandardErrorMessage>> handleMachineNotFoundException(
      MachineNotFoundException ex, WebRequest request) {
    StandardErrorMessage message = getStandardErrorMessage(ex, request);
    return StandardResponse.error(MACHINE_NOT_FOUND, message);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<StandardResponse<StandardErrorMessage>> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    StandardErrorMessage message = getStandardErrorMessage(ex, request);
    return StandardResponse.error(ObjectValidatorErrors.MessageResponseEnum.INVALID_PARAMETER, message);

  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<StandardResponse<StandardErrorMessage>> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex, WebRequest request) {
    StandardErrorMessage message = getStandardErrorMessage(ex, request);
    return StandardResponse.error(ObjectValidatorErrors.MessageResponseEnum.INVALID_PARAMETER, message);
  }


  @ExceptionHandler(MachineIdentifierAlreadyInUseException.class)
  public ResponseEntity<StandardResponse<StandardErrorMessage>> handleMachineIdentifierAlreadyInUseException(
      MachineIdentifierAlreadyInUseException ex, WebRequest request) {
    StandardErrorMessage message = getStandardErrorMessage(ex, request);
    return StandardResponse.error(ObjectValidatorErrors.MessageResponseEnum.MACHINE_IDENTIFIER_ALREADY_IN_USE, message);
  }

  private static StandardErrorMessage getStandardErrorMessage(Exception ex, WebRequest request) {
    String messageDetail = ex.getLocalizedMessage();
    final String logHeader = LOG_PREFIX + "WARN";
    log.warn(logHeader, messageDetail, ex);
    return StandardErrorMessage.builder()
        .details(messageDetail)
        .path(request.getDescription(false).replace("uri=", ""))
        .build();
  }

}

