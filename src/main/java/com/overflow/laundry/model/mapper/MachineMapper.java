package com.overflow.laundry.model.mapper;

import com.overflow.laundry.model.Machine;
import com.overflow.laundry.model.dto.MachineDto;
import org.springframework.stereotype.Component;

@Component
public class MachineMapper {

  public MachineDto toDto(Machine machine) {
    return MachineDto.builder()
        .id(machine.getId())
        .identifier(machine.getIdentifier())
        .condominium(machine.getCondominium())
        .type(machine.getType())
        .build();
  }

  public Machine toEntity(MachineDto machineDto) {
    return new Machine(machineDto.id(),
        machineDto.identifier(),
        machineDto.condominium(),
        machineDto.type());

  }
}