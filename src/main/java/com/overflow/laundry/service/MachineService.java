package com.overflow.laundry.service;

import com.overflow.laundry.model.dto.MachineDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.model.dto.PaginationResponseDto;

public interface MachineService {
  MachineDto createMachine(MachineDto machineDto);

  MachineDto getMachineById(Long id);

  MachineDto updateMachine(MachineDto machineDto);

  void deleteMachine(Long id);

  PaginationResponseDto<MachineDto> getAllMachines(PaginationRequestDto paginationRequestDto);

  MachineDto getMachineByIdentifier(String identifier);
}
