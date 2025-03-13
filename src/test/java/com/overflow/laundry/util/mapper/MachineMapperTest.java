package com.overflow.laundry.util.mapper;


import com.overflow.laundry.model.Machine;
import com.overflow.laundry.model.dto.MachineDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MachineMapperTest {

    MachineMapper machineMapper = new MachineMapper();

    @Test
    void should_returnMachineDto_whenToDtoIsCalled() {

        Machine machine = getMachineEntity();
        MachineDto machineDto = getMachineDto();
        assertEquals(machineDto, machineMapper.toDto(machine));

    }

    @Test
    void should_returnMachine_whenToEntityIsCalled() {
        Machine machine = getMachineEntity();
        MachineDto machineDto = getMachineDto();

        assertEquals(machine, machineMapper.toEntity(machineDto));

    }

    private static MachineDto getMachineDto() {
        return MachineDto.builder()
                .id(1L)
                .identifier("Washing Machine")
                .condominium("Condominium")
                .type("Washer")
                .build();
    }

    private static Machine getMachineEntity() {
        return new Machine(1L, "Washing Machine", "Condominium", "Washer");
    }
}

