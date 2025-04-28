package com.overflow.laundry.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectValidatorErrors {

  public static final String MACHINE_IDENTIFIER_NOT_EMPTY_NULL = "Machine identifier must not be empty or null";
  public static final String MACHINE_CONDOMINIUM_ID_NOT_EMPTY_NULL =
      "Machine condominium ID must not be provided for creation";
  public static final String MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL = "Machine type must not be empty or null";
  public static final String MACHINE_ID_IS_PROVIDED_ON_CREATION = "Machine ID should NOT be provided for creation";

  public static final String CONDOMINIUM_EMAIL_NOT_EMPTY_NULL = "Condominium email must not be empty or null";
  public static final String CONDOMINIUM_EMAIL_FORMAT_NOT_VALID = "Condominium email format is not valid";
  public static final String CONDOMINIUM_CONTACT_NOT_EMPTY_NULL = "Condominium contact phone must not be empty or null";
  public static final String CONDOMINIUM_ADDRESS_NOT_EMPTY_NULL = "Condominium Address must not be empty or null";
  public static final String CONDOMINIUM_NAME_NOT_EMPTY_NULL = "Condominium name must not be empty or null";

  public static final String PAGINATION_PAGE_INVALID = "Page must be a non-negative integer higher than 0.";
  public static final String PAGINATION_SIZE_INVALID = "Size must be a positive integer.";
  public static final String PAGINATION_DIRECTION_FORMAT_INVALID = "Direction must be ASC or DESC";


}
