package com.overflow.laundry.service.impl;

import com.overflow.laundry.exception.MachineIdentifierAlreadyInUseException;
import com.overflow.laundry.exception.MachineNotFoundException;
import com.overflow.laundry.model.Machine;
import com.overflow.laundry.model.dto.MachineDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.model.dto.PaginationResponseDto;
import com.overflow.laundry.repository.MachineRepository;
import com.overflow.laundry.service.MachineService;
import com.overflow.laundry.util.PaginationUtils;
import com.overflow.laundry.model.mapper.MachineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.overflow.laundry.constant.ObjectValidatorErrors.MessageResponseEnum.MACHINE_NOT_FOUND;
import static com.overflow.laundry.constant.ObjectValidatorErrors.MessageResponseEnum.MACHINE_IDENTIFIER_ALREADY_IN_USE;

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
    machineRepository.findMachineByIdentifier(machineDto.identifier())
        .ifPresent(machine -> {
          throw new MachineIdentifierAlreadyInUseException(MACHINE_IDENTIFIER_ALREADY_IN_USE.label);
        });
    Machine machine = machineMapper.toEntity(machineDto);
    return machineMapper.toDto(machineRepository.save(machine));
  }

  @Override
  public MachineDto getMachineById(Long id) {
    Optional<Machine> machine = machineRepository.findById(id);
    if (machine.isEmpty()) {
      throw new MachineNotFoundException(MACHINE_NOT_FOUND.label);
    }
    return machineMapper.toDto(machine.get());
  }

  @Override
  public MachineDto updateMachine(MachineDto machineDto) {
    if (!machineRepository.existsById(machineDto.id())) {
      throw new MachineNotFoundException(MACHINE_NOT_FOUND.label);
    }
    Machine machine = machineMapper.toEntity(machineDto);
    return machineMapper.toDto(machineRepository.save(machine));
  }

  @Override
  public void deleteMachine(Long id) {
    if (!machineRepository.existsById(id)) {
      throw new MachineNotFoundException(MACHINE_NOT_FOUND.label);
    }
    machineRepository.deleteById(id);
  }

  @Override
  public PaginationResponseDto<MachineDto> getAllMachines(PaginationRequestDto paginationRequestDto) {
    Pageable pageable = PaginationUtils.toPageable(paginationRequestDto);
    Page<Machine> allMachines = machineRepository.findAll(pageable);
    List<MachineDto> listMachineDto = allMachines.stream().map(machineMapper::toDto).toList();
    return PaginationResponseDto.<MachineDto>builder()
        .content(listMachineDto)
        .totalPages(allMachines.getTotalPages())
        .totalElements(allMachines.getTotalElements())
        .size(allMachines.getSize())
        .page(allMachines.getNumber())
        .empty(allMachines.isEmpty())
        .last(allMachines.isLast())
        .first(allMachines.isFirst())
        .build();
  }

  @Override
  public MachineDto getMachineByIdentifier(String identifier) {

    Optional<Machine> machine = Optional.of(machineRepository.findMachineByIdentifier(identifier)
        .orElseThrow(() -> new MachineNotFoundException(MACHINE_NOT_FOUND.label)));
    return machineMapper.toDto(machine.get());
  }
}
