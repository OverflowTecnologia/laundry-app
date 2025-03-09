package com.overflow.laundry.service;

import com.overflow.laundry.model.dto.MachineDto;

public interface MachineService {
    MachineDto createMachine(MachineDto machineDto);

    MachineDto getMachineById(Long id);

    MachineDto updateMachine(MachineDto machineDto);

    void deleteMachine(Long id);
}
