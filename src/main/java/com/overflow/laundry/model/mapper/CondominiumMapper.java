package com.overflow.laundry.model.mapper;

import com.overflow.laundry.model.Condominium;
import com.overflow.laundry.model.dto.CondominiumRequestDto;
import com.overflow.laundry.model.dto.CondominiumResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CondominiumMapper {

  public CondominiumResponseDto toDto(Condominium condominium) {
    return CondominiumResponseDto.builder()
        .id(condominium.getId())
        .name(condominium.getName())
        .address(condominium.getAddress())
        .contactPhone(condominium.getContactPhone())
        .email(condominium.getEmail())
        .build();
  }

  public Condominium toEntity(CondominiumRequestDto condominiumRequestDto) {
    return new Condominium(condominiumRequestDto.id(),
        condominiumRequestDto.name(),
        condominiumRequestDto.address(),
        condominiumRequestDto.contactPhone(),
        condominiumRequestDto.email(),
        null);
  }
}
