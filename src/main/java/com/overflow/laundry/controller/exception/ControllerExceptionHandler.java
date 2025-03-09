package com.overflow.laundry.controller.exception;

import com.overflow.laundry.constant.HandlerErrors;
import com.overflow.laundry.constant.ValidatorErrors;
import com.overflow.laundry.exception.ErrorResponse;
import com.overflow.laundry.exception.MachineNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);
    private static final String LOG_PREFIX = "[LAUNDRY-APP]";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorList = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            errorList.add(error.getDefaultMessage());
        });
        ErrorResponse errorResponse = buildErrorResponse(HandlerErrors.INVALID_PARAMETER, errorList, ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        List<String> errorList = new ArrayList<>();
        errorList.add(ex.getName() + " should be of type " + ex.getRequiredType().getName());
        ErrorResponse errorResponse = buildErrorResponse(HandlerErrors.INVALID_PARAMETER, errorList, ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MachineNotFoundException.class)
    public ResponseEntity<Object> handleMachineNotFoundException(MachineNotFoundException ex) {
        ErrorResponse errorResponse = buildErrorResponse(HandlerErrors.NOT_FOUND, List.of(ValidatorErrors.MACHINE_NOT_FOUND), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    private ErrorResponse buildErrorResponse(String message, List<String> detail, Exception ex) {
        String logHeader = LOG_PREFIX + "Error message:" + message;
        log.warn(logHeader, detail, ex);
        return new ErrorResponse(message, detail);
    }
}

