package com.overflow.laundry.model.dto;

import lombok.Builder;

import static com.overflow.laundry.constant.ObjectValidatorErrors.PAGINATION_DIRECTION_FORMAT_INVALID;
import static com.overflow.laundry.constant.ObjectValidatorErrors.PAGINATION_PAGE_INVALID;
import static com.overflow.laundry.constant.ObjectValidatorErrors.PAGINATION_SIZE_INVALID;

@Builder
public record PaginationRequestDto(
    Integer page,
    Integer size,
    String sortBy,
    String direction
) {

  public PaginationRequestDto(Integer page, Integer size, String sortBy, String direction) {
    this.page = page == null ? 1 : page;
    this.size = size == null ? 10 : size;
    this.sortBy = sortBy == null ? "id" : sortBy;
    this.direction = direction == null ? "DESC" : direction.toUpperCase();
    validate(this);
  }

  private void validate(PaginationRequestDto paginationRequestDto) {
    if (page != null && page <= 0) {
      throw new IllegalArgumentException(PAGINATION_PAGE_INVALID);
    }
    if (size != null && size <= 0) {
      throw new IllegalArgumentException(PAGINATION_SIZE_INVALID);
    }
    if (!sortBy.isEmpty()
        && !direction.equals("ASC")
        && !direction.equals("DESC")) {
      throw new IllegalArgumentException(PAGINATION_DIRECTION_FORMAT_INVALID);
    }
  }
}

