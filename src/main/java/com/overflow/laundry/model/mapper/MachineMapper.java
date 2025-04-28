package com.overflow.laundry.model.mapper;

import com.overflow.laundry.model.Condominium;
import com.overflow.laundry.model.Machine;
import com.overflow.laundry.model.dto.MachineRequestDto;
import com.overflow.laundry.model.dto.MachineResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MachineMapper {

  private final CondominiumMapper condominiumMapper;

  @Autowired
  public MachineMapper(CondominiumMapper condominiumMapper) {
    this.condominiumMapper = condominiumMapper;
  }

  public MachineResponseDto toDto(Machine machine) {

    return MachineResponseDto.builder()
        .id(machine.getId())
        .identifier(machine.getIdentifier())
        .condominium(condominiumMapper.toDto(machine.getCondominium()))
        .type(machine.getType())
        .build();
  }

  public Machine toEntity(MachineRequestDto machineRequestDto, Condominium condominium) {
    return new Machine(machineRequestDto.id(),
        machineRequestDto.identifier(),
        machineRequestDto.type(),
        condominium);

  }
}