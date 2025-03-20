package com.overflow.laundry.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectValidatorErrors {

  public static final String MACHINE_IDENTIFIER_NOT_EMPTY_NULL = "Machine identifier must not be empty or null";
  public static final String MACHINE_CONDOMINIUM_NOT_EMPTY_NULL = "Machine condominium must not be empty or null";
  public static final String MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL = "Machine type must not be empty or null";


  public static final String PAGINATION_PAGE_INVALID = "Page must be a non-negative integer higher than 0.";
  public static final String PAGINATION_SIZE_INVALID = "Size must be a positive integer.";
  public static final String PAGINATION_DIRECTION_FORMAT_INVALID = "Direction must be ASC or DESC";

  public enum MessageResponseEnum {

    MACHINE_CREATED(HttpStatus.CREATED, "Machine created successfully"),
    MACHINE_FOUND(HttpStatus.OK, "Machine found"),
    MACHINE_UPDATED(HttpStatus.ACCEPTED, "Machine updated successfully"),
    MACHINE_DELETED(HttpStatus.NO_CONTENT, "Machine deleted successfully"),
    MACHINE_NOT_FOUND(HttpStatus.NOT_FOUND, "Machine not found"),
    MACHINE_IDENTIFIER_ALREADY_IN_USE(HttpStatus.CONFLICT, "Machine identifier already in use"),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request"),
    SUCCESS(HttpStatus.OK, "Success"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");


    public final HttpStatus status;
    public final String label;

    MessageResponseEnum(HttpStatus status, String label) {
      this.status = status;
      this.label = label;
    }
  }
}
