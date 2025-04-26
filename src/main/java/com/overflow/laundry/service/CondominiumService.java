package com.overflow.laundry.service;

import com.overflow.laundry.model.dto.CondominiumRequestDto;
import com.overflow.laundry.model.dto.CondominiumResponseDto;

public interface CondominiumService {
  CondominiumResponseDto createCondominium(CondominiumRequestDto condominiumRequestDto);

  CondominiumResponseDto getCondominiumById(Long id);
}
