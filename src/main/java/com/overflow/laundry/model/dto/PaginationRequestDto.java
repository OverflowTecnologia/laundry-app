package com.overflow.laundry.model.dto;

import lombok.Builder;

import static com.overflow.laundry.constant.ObjectValidatorErrors.PAGINATION_DIRECTION_FORMAT_INVALID;
import static com.overflow.laundry.constant.ObjectValidatorErrors.PAGINATION_PAGE_INVALID;
import static com.overflow.laundry.constant.ObjectValidatorErrors.PAGINATION_SIZE_INVALID;

@Builder
public record PaginationRequestDto(
    Integer pageNumber,
    Integer pageSize,
    String sortBy,
    String direction
) {

  public PaginationRequestDto(Integer pageNumber, Integer pageSize, String sortBy, String direction) {
    this.pageNumber = pageNumber == null ? 1 : pageNumber;
    this.pageSize = pageSize == null ? 10 : pageSize;
    this.sortBy = sortBy == null ? "id" : sortBy;
    this.direction = direction == null ? "DESC" : direction.toUpperCase();
    validate(this);
  }

  private void validate(PaginationRequestDto paginationRequestDto) {
    if (pageNumber != null && pageNumber <= 0) {
      throw new IllegalArgumentException(PAGINATION_PAGE_INVALID);
    }
    if (pageSize != null && pageSize <= 0) {
      throw new IllegalArgumentException(PAGINATION_SIZE_INVALID);
    }
    if (!sortBy.isEmpty()
        && !direction.equals("ASC")
        && !direction.equals("DESC")) {
      throw new IllegalArgumentException(PAGINATION_DIRECTION_FORMAT_INVALID);
    }
  }
}

