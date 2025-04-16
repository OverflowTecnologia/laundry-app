package com.overflow.laundry.util.mapper;

import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.model.dto.PaginationResponseDto;
import com.overflow.laundry.util.PaginationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaginationUtilsTest {


  @Test
  void givenPaginationRequestDto_whenToPageable_thenReturnPageable() {

    PaginationRequestDto paginationRequestDto = PaginationRequestDto.builder()
        .page(1)
        .size(10)
        .sortBy("id")
        .direction("DESC")
        .build();

    Pageable mockPageable = PageRequest.of(paginationRequestDto.page() - 1,
        paginationRequestDto.size(),
        Sort.Direction.valueOf(paginationRequestDto.direction()),
        paginationRequestDto.sortBy());

    Pageable pageableReturned = PaginationUtils.toPageable(paginationRequestDto);

    assertEquals(pageableReturned, mockPageable);
  }

  @Test
  void givenPageAndMapper_whenToPaginationResponse_thenReturnPaginationResponseDto() {
    Page mockPage = mock(Page.class);
    when(mockPage.getTotalPages()).thenReturn(5);
    when(mockPage.getTotalElements()).thenReturn(50L);
    when(mockPage.getSize()).thenReturn(10);
    when(mockPage.getNumber()).thenReturn(0);
    when(mockPage.isEmpty()).thenReturn(false);
    when(mockPage.isLast()).thenReturn(false);
    when(mockPage.isFirst()).thenReturn(false);
    when(mockPage.stream()).thenReturn(Stream.of("item1", "item2"));

    Function<String, String> mapper = Function.identity();

    PaginationResponseDto responseDto = PaginationUtils.toPaginationResponse(mockPage, mapper);

    assertEquals(5, responseDto.totalPages());
    assertEquals(50L, responseDto.totalElements());
    assertEquals(10, responseDto.size());
    assertEquals(1, responseDto.page());
    assertFalse(responseDto.empty());
    assertFalse(responseDto.last());
    assertFalse(responseDto.first());
    assertEquals(List.of("item1", "item2"), responseDto.content());
  }
}
