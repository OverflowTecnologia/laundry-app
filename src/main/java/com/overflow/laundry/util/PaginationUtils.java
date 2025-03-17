package com.overflow.laundry.util;

import com.overflow.laundry.model.dto.PaginationRequestDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationUtils {

  public static Pageable toPageable(PaginationRequestDto paginationRequestDto) {
    int pageNumber = paginationRequestDto.page() - 1; // Adjust to 0-based index
    return PageRequest.of(pageNumber,
        paginationRequestDto.size(),
        Sort.Direction.valueOf(paginationRequestDto.direction()),
        paginationRequestDto.sortBy());
  }
}
