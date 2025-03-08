package com.overflow.laundry.service.impl;

import com.overflow.laundry.model.dto.MachineDto;
import com.overflow.laundry.service.MachineService;
import org.springframework.stereotype.Service;

@Service
public class MachineServiceImpl implements MachineService {

    @Override
    public MachineDto createMachine(MachineDto machineDto) {
        return null;
    }

    @Override
    public MachineDto getMachineById(Long id) {
        return null;
    }

    @Override
    public MachineDto updateMachine(MachineDto machineDto) {
        //verify if machine exists
        //if not, throw new MachineNotFoundException("Machine not found");

        return null;
    }

    @Override
    public boolean deleteMachine(Long id) {
        return false;
    }
}
