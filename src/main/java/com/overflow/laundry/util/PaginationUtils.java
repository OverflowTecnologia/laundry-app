package com.overflow.laundry.util;

import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.model.dto.PaginationResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationUtils {

  public static Pageable toPageable(PaginationRequestDto paginationRequestDto) {
    int pageNumber = paginationRequestDto.page() - 1; // Adjusting page number to be 1-based
    return PageRequest.of(pageNumber,
        paginationRequestDto.size(),
        Sort.Direction.valueOf(paginationRequestDto.direction()),
        paginationRequestDto.sortBy());
  }

  public static <T, U> PaginationResponseDto<U> toPaginationResponse(Page<T> page, Function<T, U> mapper) {
    List<U> content = page.stream().map(mapper).toList();
    return PaginationResponseDto.<U>builder()
        .content(content)
        .totalPages(page.getTotalPages())
        .totalElements(page.getTotalElements())
        .size(page.getSize())
        .page(page.getNumber() + 1) // Adjusting page number to be 1-based
        .empty(page.isEmpty())
        .last(page.isLast())
        .first(page.isFirst())
        .build();
  }
}
