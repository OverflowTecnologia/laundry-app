package com.overflow.laundry.util.mapper;

import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.util.PaginationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaginationUtilsTest {


  @Test
  void givenPaginationRequestDto_whenToPageable_thenReturnPageable() {

    PaginationRequestDto paginationRequestDto = PaginationRequestDto.builder()
        .page(0)
        .size(10)
        .sortBy("id")
        .direction("DESC")
        .build();

    Pageable mockPageable = PageRequest.of(paginationRequestDto.page(),
        paginationRequestDto.size(),
        Sort.by(paginationRequestDto.direction(), paginationRequestDto.sortBy()));

    Pageable pageableReturned = PaginationUtils.toPageable(paginationRequestDto);

    assertEquals(pageableReturned, mockPageable);
  }
}
