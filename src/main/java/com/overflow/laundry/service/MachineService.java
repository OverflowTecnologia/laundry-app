package com.overflow.laundry.service;

import com.overflow.laundry.model.dto.MachineDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import org.springframework.data.domain.Page;

public interface MachineService {
  MachineDto createMachine(MachineDto machineDto);

  MachineDto getMachineById(Long id);

  MachineDto updateMachine(MachineDto machineDto);

  void deleteMachine(Long id);

  Page<MachineDto> getAllMachines(PaginationRequestDto paginationRequestDto);
}
