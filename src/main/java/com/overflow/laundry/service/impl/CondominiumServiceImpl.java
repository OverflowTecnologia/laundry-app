package com.overflow.laundry.service.impl;

import com.overflow.laundry.model.Condominium;
import com.overflow.laundry.model.dto.CondominiumDto;
import com.overflow.laundry.model.mapper.CondominiumMapper;
import com.overflow.laundry.repository.CondominiumRepository;
import com.overflow.laundry.service.CondominiumService;
import org.springframework.stereotype.Service;

@Service
public class CondominiumServiceImpl implements CondominiumService {

  private final CondominiumRepository condominiumRepository;
  private final CondominiumMapper condominiumMapper;

  public CondominiumServiceImpl(CondominiumRepository condominiumRepository, CondominiumMapper condominiumMapper) {
    this.condominiumRepository = condominiumRepository;
    this.condominiumMapper = condominiumMapper;
  }

  @Override
  public CondominiumDto createCondominium(CondominiumDto condominiumDto) {
    if (condominiumDto.id() != null) {
      throw new IllegalArgumentException("Condominium ID should NOT be provided for creation");
    }
    Condominium condominiumEntity = condominiumMapper.toEntity(condominiumDto);
    return condominiumMapper.toDto(condominiumRepository.save(condominiumEntity));
  }
}
