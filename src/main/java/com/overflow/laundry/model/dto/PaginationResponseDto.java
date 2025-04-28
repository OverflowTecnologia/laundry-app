package com.overflow.laundry.model.dto;

import lombok.Builder;

import java.util.Collection;

@Builder
public record PaginationResponseDto<T>(
    Collection<T> content,
    Integer totalPages,
    long totalElements,
    Integer pageSize,
    Integer pageNumber,
    boolean empty,
    boolean last,
    boolean first
) {
}

