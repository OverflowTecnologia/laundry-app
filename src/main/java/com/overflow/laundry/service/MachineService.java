package com.overflow.laundry.service;

import com.overflow.laundry.model.dto.MachineRequestDto;
import com.overflow.laundry.model.dto.MachineResponseDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.model.dto.PaginationResponseDto;

public interface MachineService {
  MachineResponseDto createMachine(MachineRequestDto machineRequestDto);

  MachineResponseDto getMachineById(Long id);

  MachineResponseDto updateMachine(MachineRequestDto machineRequestDto);

  void deleteMachine(Long id);

  PaginationResponseDto<MachineResponseDto> getAllMachines(PaginationRequestDto paginationRequestDto);

  MachineResponseDto getMachineByIdentifier(String identifier);
}
