package com.overflow.laundry.service;

import com.overflow.laundry.model.dto.CondominiumRequestDto;
import com.overflow.laundry.model.dto.CondominiumResponseDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.model.dto.PaginationResponseDto;

public interface CondominiumService {
  CondominiumResponseDto createCondominium(CondominiumRequestDto condominiumRequestDto);

  CondominiumResponseDto getCondominiumById(Long id);

  PaginationResponseDto<CondominiumResponseDto> getAllCondominiums(PaginationRequestDto paginationRequestDto);
}
