package com.overflow.laundry.exception;

import com.overflow.laundry.config.StandardResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Set;
import java.util.stream.Collectors;

import static com.overflow.laundry.constant.MessageResponseEnum.BAD_REQUEST;
import static com.overflow.laundry.constant.MessageResponseEnum.INTERNAL_SERVER_ERROR;
import static com.overflow.laundry.constant.MessageResponseEnum.INVALID_PARAMETER;
import static com.overflow.laundry.constant.MessageResponseEnum.MACHINE_IDENTIFIER_ALREADY_IN_USE;
import static com.overflow.laundry.constant.MessageResponseEnum.MACHINE_NOT_FOUND;
import static com.overflow.laundry.constant.MessageResponseEnum.NOT_FOUND;
import static com.overflow.laundry.util.LogUtils.logWarn;


@RestControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<StandardResponse<StandardErrorMessage>> handleAllExceptions(
      Exception ex, WebRequest request) {
    StandardErrorMessage message = getStandardErrorMessage(ex, request);
    return StandardResponse.error(INTERNAL_SERVER_ERROR, message);
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

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<StandardResponse<StandardErrorMessage>> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {

    Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
    String violationsMessages = constraintViolations.stream()
        .map(this::formatConstraintViolationMessage)
        .collect(Collectors.joining(", "));

    StandardErrorMessage standardErrorMessage = StandardErrorMessage.builder()
        .details(violationsMessages)
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return StandardResponse.error(INVALID_PARAMETER, standardErrorMessage);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<StandardResponse<StandardErrorMessage>> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException ex, WebRequest request) {
    StandardErrorMessage message = getStandardErrorMessage(ex, request);
    return StandardResponse.error(INVALID_PARAMETER, message);
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
    return StandardResponse.error(INVALID_PARAMETER, message);

  }

  @ExceptionHandler(MachineIdentifierAlreadyInUseException.class)
  public ResponseEntity<StandardResponse<StandardErrorMessage>> handleMachineIdentifierAlreadyInUseException(
      MachineIdentifierAlreadyInUseException ex, WebRequest request) {
    StandardErrorMessage message = getStandardErrorMessage(ex, request);
    return StandardResponse.error(MACHINE_IDENTIFIER_ALREADY_IN_USE, message);
  }

  @ExceptionHandler(CondominiumNotFoundException.class)
  public ResponseEntity<StandardResponse<StandardErrorMessage>> handleCondominiumNotFoundException(
      CondominiumNotFoundException ex, WebRequest request) {
    StandardErrorMessage message = getStandardErrorMessage(ex, request);
    return StandardResponse.error(NOT_FOUND, message);
  }

  private String formatConstraintViolationMessage(ConstraintViolation<?> violation) {
    String fieldName = violation.getPropertyPath().toString();
    fieldName = fieldName.contains(".") ? fieldName.substring(fieldName.lastIndexOf('.') + 1) : fieldName;
    return "Parameter '" + fieldName + "' " + violation.getMessage();
  }

  private static StandardErrorMessage getStandardErrorMessage(Exception ex, WebRequest request) {
    String messageDetail = ex.getLocalizedMessage();
    logWarn(messageDetail, ex);
    return StandardErrorMessage.builder()
        .details(messageDetail)
        .path(request.getDescription(false).replace("uri=", ""))
        .build();
  }

}
