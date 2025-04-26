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
import static com.overflow.laundry.constant.ObjectValidatorErrors.MACHINE_ID_IS_PROVIDED_ON_CREATION;


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
      throw new IllegalArgumentException(MACHINE_ID_IS_PROVIDED_ON_CREATION);
    }

    Condominium condominiumEntity = condominiumRepository.findById(machineRequestDto.condominiumId())
        .orElseThrow(() -> new CondominiumNotFoundException(CONDOMINIUM_NOT_FOUND.label));
    machineRepository.findMachineByCondominiumIdAndIdentifier(machineRequestDto.identifier(),
            machineRequestDto.condominiumId())
        .ifPresent(machine -> {
          throw new MachineIdentifierAlreadyInUseException(MACHINE_IDENTIFIER_ALREADY_IN_USE.label);
        });

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
    Condominium condominiumEntity = condominiumRepository.findById(machineRequestDto.condominiumId())
        .orElseThrow(() -> new CondominiumNotFoundException(CONDOMINIUM_NOT_FOUND.label));
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
        .page(allMachines.getNumber() + 1) // Adjusting page number to be 1-based
        .empty(allMachines.isEmpty())
        .last(allMachines.isLast())
        .first(allMachines.isFirst())
        .build();
  }

  @Override
  public MachineResponseDto getMachineByCondominiumAndIdentifier(Long condominiumId, String identifier) {

    Optional<Machine> machine = Optional.of(machineRepository.findMachineByCondominiumIdAndIdentifier(
        identifier, condominiumId)
        .orElseThrow(() -> new MachineNotFoundException(MACHINE_NOT_FOUND.label)));
    return machineMapper.toDto(machine.get());
  }
}
