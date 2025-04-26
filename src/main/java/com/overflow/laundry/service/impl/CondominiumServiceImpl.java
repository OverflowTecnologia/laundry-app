package com.overflow.laundry.service.impl;

import com.overflow.laundry.exception.CondominiumNotFoundException;
import com.overflow.laundry.model.Condominium;
import com.overflow.laundry.model.dto.CondominiumRequestDto;
import com.overflow.laundry.model.dto.CondominiumResponseDto;
import com.overflow.laundry.model.mapper.CondominiumMapper;
import com.overflow.laundry.repository.CondominiumRepository;
import com.overflow.laundry.service.CondominiumService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.overflow.laundry.constant.MessageResponseEnum.CONDOMINIUM_NOT_FOUND;

@Service
public class CondominiumServiceImpl implements CondominiumService {

  private final CondominiumRepository condominiumRepository;
  private final CondominiumMapper condominiumMapper;

  public CondominiumServiceImpl(CondominiumRepository condominiumRepository, CondominiumMapper condominiumMapper) {
    this.condominiumRepository = condominiumRepository;
    this.condominiumMapper = condominiumMapper;
  }

  @Override
  public CondominiumResponseDto createCondominium(CondominiumRequestDto condominiumRequestDto) {
    if (condominiumRequestDto.id() != null) {
      throw new IllegalArgumentException("Condominium ID should NOT be provided for creation");
    }
    Condominium condominiumEntity = condominiumMapper.toEntity(condominiumRequestDto);
    return condominiumMapper.toDto(condominiumRepository.save(condominiumEntity));
  }

  @Override
  public CondominiumResponseDto getCondominiumById(Long id) {
    Optional<Condominium> condominiumFound = condominiumRepository.findById(id);
    if (condominiumFound.isEmpty()) {
      throw new CondominiumNotFoundException(CONDOMINIUM_NOT_FOUND.label);
    }
    return condominiumMapper.toDto(condominiumFound.get());
  }
}
