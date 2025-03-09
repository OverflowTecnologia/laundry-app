package com.overflow.laundry.util.mapper;

import com.overflow.laundry.model.Machine;
import com.overflow.laundry.model.dto.MachineDto;
import org.springframework.stereotype.Component;

@Component
public class MachineMapper {

    public MachineDto toDto(Machine machine) {
        return new MachineDto(
                machine.getId(),
                machine.getIdentifier(),
                machine.getCondominium(),
                machine.getTypeOfMachine()
        );
    }

    public Machine toEntity(MachineDto machineDto) {
        return new Machine(machineDto.id(),
                machineDto.identifier(),
                machineDto.condominium(),
                machineDto.typeOfMachine());

    }
}