package com.overflow.laundry.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectValidatorErrors {

  public static final String MACHINE_IDENTIFIER_NOT_EMPTY_NULL = "Machine identifier must not be empty or null";
  public static final String MACHINE_CONDOMINIUM_NOT_EMPTY_NULL = "Machine condominium must not be empty or null";
  public static final String MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL = "Machine type must not be empty or null";
  public static final String MACHINE_NOT_FOUND = "Machine not found";

  public static final String PAGINATION_PAGE_INVALID = "Page must be a non-negative integer higher than 0.";
  public static final String PAGINATION_SIZE_INVALID = "Size must be a positive integer.";
  public static final String PAGINATION_DIRECTION_FORMAT_INVALID = "Direction must be ASC or DESC";

}
