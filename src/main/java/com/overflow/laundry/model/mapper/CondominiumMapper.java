package com.overflow.laundry.model.mapper;

import com.overflow.laundry.model.Condominium;
import com.overflow.laundry.model.dto.CondominiumDto;
import org.springframework.stereotype.Component;

@Component
public class CondominiumMapper {

  public CondominiumDto toDto(Condominium condominium) {
    return CondominiumDto.builder()
        .id(condominium.getId())
        .name(condominium.getName())
        .address(condominium.getAddress())
        .contactPhone(condominium.getContactPhone())
        .email(condominium.getEmail())
        .build();
  }

  public Condominium toEntity(CondominiumDto condominiumDto) {
    return new Condominium(condominiumDto.id(),
        condominiumDto.name(),
        condominiumDto.address(),
        condominiumDto.contactPhone(),
        condominiumDto.email(),
        null);
  }
}
