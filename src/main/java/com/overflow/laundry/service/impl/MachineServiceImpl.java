package com.overflow.laundry.service.impl;

import com.overflow.laundry.exception.MachineNotFoundException;
import com.overflow.laundry.model.Machine;
import com.overflow.laundry.model.dto.MachineDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.repository.MachineRepository;
import com.overflow.laundry.service.MachineService;
import com.overflow.laundry.util.PaginationUtils;
import com.overflow.laundry.util.mapper.MachineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MachineServiceImpl implements MachineService {

  private final MachineRepository machineRepository;
  private final MachineMapper machineMapper;

  @Autowired
  public MachineServiceImpl(MachineRepository machineRepository, MachineMapper machineMapper) {
    this.machineRepository = machineRepository;
    this.machineMapper = machineMapper;
  }

  @Override
  public MachineDto createMachine(MachineDto machineDto) {
    Machine machine = machineMapper.toEntity(machineDto);
    return machineMapper.toDto(machineRepository.save(machine));
  }

  @Override
  public MachineDto getMachineById(Long id) {
    Optional<Machine> machine = machineRepository.findById(id);
    if (machine.isEmpty()) {
      throw new MachineNotFoundException("Machine not found");
    }
    return machineMapper.toDto(machine.get());
  }

  @Override
  public MachineDto updateMachine(MachineDto machineDto) {
    if (!machineRepository.existsById(machineDto.id())) {
      throw new MachineNotFoundException("Machine not found");
    }
    Machine machine = machineMapper.toEntity(machineDto);
    return machineMapper.toDto(machineRepository.save(machine));
  }

  @Override
  public void deleteMachine(Long id) {
    if (!machineRepository.existsById(id)) {
      throw new MachineNotFoundException("Machine not found");
    }
    machineRepository.deleteById(id);
  }

  @Override
  public Page<MachineDto> getAllMachines(PaginationRequestDto paginationRequestDto) {
    Pageable pageable = PaginationUtils.toPageable(paginationRequestDto);
    Page<Machine> allMachines = machineRepository.findAll(pageable);
    return allMachines.map(machineMapper::toDto);
  }
}
