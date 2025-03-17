package com.overflow.laundry.model.dto;

import lombok.Builder;

import java.util.Collection;

@Builder
public record PaginationResponseDto<T>(
    Collection<T> content,
    Integer totalPages,
    long totalElements,
    Integer size,
    Integer page,
    boolean empty,
    boolean last,
    boolean first
) {
  public PaginationResponseDto(Collection<T> content, Integer totalPages, long totalElements, Integer size,
                               Integer page, boolean empty, boolean last, boolean first) {
    this.content = content;
    this.totalPages = totalPages;
    this.totalElements = totalElements;
    this.size = size;
    this.page = page + 1;
    this.empty = empty;
    this.last = last;
    this.first = first;
  }
}

