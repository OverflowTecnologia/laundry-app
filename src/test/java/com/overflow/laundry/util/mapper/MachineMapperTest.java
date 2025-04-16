package com.overflow.laundry.util.mapper;

import com.overflow.laundry.model.Condominium;
import com.overflow.laundry.model.Machine;
import com.overflow.laundry.model.dto.CondominiumDto;
import com.overflow.laundry.model.dto.MachineRequestDto;
import com.overflow.laundry.model.dto.MachineResponseDto;
import com.overflow.laundry.model.mapper.CondominiumMapper;
import com.overflow.laundry.model.mapper.MachineMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MachineMapperTest {

  CondominiumMapper condominiumMapper = new CondominiumMapper();
  MachineMapper machineMapper = new MachineMapper(condominiumMapper);

  @Test
  void should_returnMachineDto_whenToDtoIsCalled() {

    Machine machine = getMockMachineEntity();
    MachineResponseDto machineDto = getMachineResponseDto();
    assertEquals(machineDto, machineMapper.toDto(machine));

  }

  @Test
  void should_returnMachine_whenToEntityIsCalled() {

    Machine machine = getMockMachineEntity();
    MachineRequestDto machineRequestDto = getMachineRequestDto();
    assertEquals(machine, machineMapper.toEntity(machineRequestDto, getMockCondominium()));

  }

  private static MachineResponseDto getMachineResponseDto() {
    return MachineResponseDto.builder().id(1L).identifier("Washing Machine")
        .condominium(getMockCondominiumDto()).type("Washer").build();
  }

  private static MachineRequestDto getMachineRequestDto() {
    return MachineRequestDto.builder().id(1L).identifier("Washing Machine").condominiumId(1L).type("Washer").build();
  }

  private static Machine getMockMachineEntity() {
    return new Machine(1L, "Washing Machine", "Washer", getMockCondominium());
  }

  private static Condominium getMockCondominium() {
    return new Condominium(1L, "Condominium 1", "123 Main St", "123456789", "test@test.com", null);
  }

  private static CondominiumDto getMockCondominiumDto() {
    return CondominiumDto.builder()
        .id(1L)
        .name("Condominium 1")
        .email("test@test.com")
        .address("123 Main St")
        .contactPhone("123456789")
        .build();
  }
}

