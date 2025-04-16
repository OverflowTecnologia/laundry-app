package com.overflow.laundry.service.impl;

import com.overflow.laundry.exception.CondominiumNotFoundException;
import com.overflow.laundry.exception.MachineIdentifierAlreadyInUseException;
import com.overflow.laundry.exception.MachineNotFoundException;
import com.overflow.laundry.model.Condominium;
import com.overflow.laundry.model.Machine;
import com.overflow.laundry.model.dto.MachineRequestDto;
import com.overflow.laundry.model.dto.MachineResponseDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.model.dto.PaginationResponseDto;
import com.overflow.laundry.model.mapper.MachineMapper;
import com.overflow.laundry.repository.CondominiumRepository;
import com.overflow.laundry.repository.MachineRepository;
import com.overflow.laundry.service.MachineService;
import com.overflow.laundry.util.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.overflow.laundry.constant.MessageResponseEnum.CONDOMINIUM_NOT_FOUND;
import static com.overflow.laundry.constant.MessageResponseEnum.MACHINE_IDENTIFIER_ALREADY_IN_USE;
import static com.overflow.laundry.constant.MessageResponseEnum.MACHINE_NOT_FOUND;


@Service
public class MachineServiceImpl implements MachineService {

  private final CondominiumRepository condominiumRepository;
  private final MachineRepository machineRepository;
  private final MachineMapper machineMapper;

  @Autowired
  public MachineServiceImpl(MachineRepository machineRepository,
                            MachineMapper machineMapper,
                            CondominiumRepository condominiumRepository) {
    this.machineRepository = machineRepository;
    this.machineMapper = machineMapper;
    this.condominiumRepository = condominiumRepository;
  }

  @Override
  public MachineResponseDto createMachine(MachineRequestDto machineRequestDto) {

    if (machineRequestDto.id() != null) {
      throw new IllegalArgumentException("Machine ID should NOT be provided for creation");
    }
    if (machineRequestDto.condominiumId() == null) {
      throw new IllegalArgumentException("Condominium ID should NOT be null");
    }
    machineRepository.findMachineByIdentifier(machineRequestDto.identifier())
        .ifPresent(machine -> {
          throw new MachineIdentifierAlreadyInUseException(MACHINE_IDENTIFIER_ALREADY_IN_USE.label);
        }); // TODO: Make it by condominum ID
    Condominium condominiumEntity = condominiumRepository.findById(machineRequestDto.condominiumId())
        .orElseThrow(() -> new CondominiumNotFoundException(CONDOMINIUM_NOT_FOUND.label));

    Machine machine = machineMapper.toEntity(machineRequestDto, condominiumEntity);
    return machineMapper.toDto(machineRepository.save(machine));
  }

  @Override
  public MachineResponseDto getMachineById(Long id) {
    Optional<Machine> machine = machineRepository.findById(id);
    if (machine.isEmpty()) {
      throw new MachineNotFoundException(MACHINE_NOT_FOUND.label);
    }
    return machineMapper.toDto(machine.get());
  }

  @Override
  public MachineResponseDto updateMachine(MachineRequestDto machineRequestDto) {
    if (!machineRepository.existsById(machineRequestDto.id())) {
      throw new MachineNotFoundException(MACHINE_NOT_FOUND.label);
    }
    if (machineRequestDto.condominiumId() == null) {
      throw new IllegalArgumentException("Condominium ID should NOT be null"); //TODO: test it
    }
    Condominium condominiumEntity = condominiumRepository.findById(machineRequestDto.condominiumId())
        .orElseThrow(() -> new CondominiumNotFoundException(CONDOMINIUM_NOT_FOUND.label)); //TODO: test it
    Machine machine = machineMapper.toEntity(machineRequestDto, condominiumEntity);
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
  public PaginationResponseDto<MachineResponseDto> getAllMachines(PaginationRequestDto paginationRequestDto) {
    Pageable pageable = PaginationUtils.toPageable(paginationRequestDto);
    Page<Machine> allMachines = machineRepository.findAll(pageable);
    List<MachineResponseDto> listMachineDto = allMachines.stream().map(machineMapper::toDto).toList();
    return PaginationResponseDto.<MachineResponseDto>builder()
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
  public MachineResponseDto getMachineByIdentifier(String identifier) {

    Optional<Machine> machine = Optional.of(machineRepository.findMachineByIdentifier(identifier)
        .orElseThrow(() -> new MachineNotFoundException(MACHINE_NOT_FOUND.label)));
    return machineMapper.toDto(machine.get());
  }
}
