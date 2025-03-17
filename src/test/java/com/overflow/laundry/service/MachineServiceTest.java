package com.overflow.laundry.service;

import com.overflow.laundry.exception.MachineNotFoundException;
import com.overflow.laundry.model.Machine;
import com.overflow.laundry.model.dto.MachineDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.repository.MachineRepository;
import com.overflow.laundry.service.impl.MachineServiceImpl;
import com.overflow.laundry.util.mapper.MachineMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MachineServiceTest {

  MachineService machineService;

  @InjectMocks
  MachineMapper machineMapper;

  @Mock
  MachineRepository machineRepository;

  @BeforeEach
  void setUp() {
    machineService = new MachineServiceImpl(machineRepository, machineMapper);
  }

  @Test
  void should_createMachine_whenCreateMachineIsCalled() {

    Machine machine = getMockMachine();
    when(machineRepository.save(any(Machine.class))).thenReturn(machine);

    MachineDto machineDto = getMachineDto();
    MachineDto machineCreated = machineService.createMachine(machineDto);

    assertEquals(machineDto, machineCreated);

  }


  @Test
  void should_returnMachineDto_whenGetMachineByIdIsCalled() {
    Machine mockMachine = getMockMachine();
    when(machineRepository.findById(any())).thenReturn(Optional.of(mockMachine));

    MachineDto machineDto = getMachineDto();
    MachineDto machineFound = machineService.getMachineById(1L);

    assertEquals(machineDto, machineFound);
  }

  @Test
  void should_thrownMachineNotFoundException_whenGetMachineByIdIsCalled() {
    when(machineRepository.findById(any())).thenReturn(Optional.empty());
    assertThrows(MachineNotFoundException.class, () -> {
      machineService.getMachineById(1L);
    });

  }

  @Test
  void should_updateMachine_whenUpdateMachineIsCalled() {
    Machine machine = getMockMachine();
    when(machineRepository.existsById(any())).thenReturn(true);
    when(machineRepository.save(any(Machine.class))).thenReturn(machine);

    MachineDto machineDto = getMachineDto();
    MachineDto machineUpdated = machineService.updateMachine(machineDto);

    assertEquals(machineDto, machineUpdated);
  }

  @Test
  void should_thrownMachineNotFoundException_whenUpdateMachineIsCalled() {
    when(machineRepository.existsById(any())).thenReturn(false);
    assertThrows(MachineNotFoundException.class, () -> {
      machineService.updateMachine(getMachineDto());
    });
    verify(machineRepository, never()).save(any());
  }

  @Test
  void should_deleteMachine_whenDeleteMachineIsCalled() {
    Long id = 1L;
    when(machineRepository.existsById(any())).thenReturn(true);
    machineService.deleteMachine(id);
    verify(machineRepository, times(1)).deleteById(id);
  }

  @Test
  void should_thrownMachineNotFoundException_whenDeleteMachineIsCalled() {
    Long id = 1L;
    when(machineRepository.existsById(any())).thenReturn(false);
    assertThrows(MachineNotFoundException.class, () -> {
      machineService.deleteMachine(id);
    });
    verify(machineRepository, never()).deleteById(id);
  }

  @Test
  void given_defaultPagination_whenGetAllMachinesIsCalled_thenReturnAllMachines() {
    Machine mockMachine = getMockMachine();
    List<Machine> machines = List.of(mockMachine);
    Page<Machine> machinePage = new PageImpl<>(machines);
    PaginationRequestDto defaultPagination = PaginationRequestDto.builder()
        .page(0)
        .size(10)
        .sortBy("id")
        .direction("DESC")
        .build();

    when(machineRepository.findAll(any(Pageable.class))).thenReturn(machinePage);
    machineService.getAllMachines(defaultPagination);
    verify(machineRepository, times(1)).findAll(any(Pageable.class));
  }

  private static Machine getMockMachine() {
    return new Machine(1L,
        "Washing Machine",
        "Condominium",
        "Washer");
  }

  private static MachineDto getMachineDto() {
    return MachineDto.builder()
        .id(1L)
        .identifier("Washing Machine")
        .condominium("Condominium")
        .type("Washer")
        .build();
  }

}
