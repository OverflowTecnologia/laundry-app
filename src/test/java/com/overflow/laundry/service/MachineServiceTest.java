package com.overflow.laundry.service;

import com.overflow.laundry.exception.MachineIdentifierAlreadyInUseException;
import com.overflow.laundry.exception.MachineNotFoundException;
import com.overflow.laundry.model.Machine;
import com.overflow.laundry.model.dto.MachineDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.repository.MachineRepository;
import com.overflow.laundry.service.impl.MachineServiceImpl;
import com.overflow.laundry.model.mapper.MachineMapper;
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
  void givenMachine_whenCreateMachineIsCalled_thenCreateMachine() {

    Machine machine = getMockMachine();
    when(machineRepository.save(any(Machine.class))).thenReturn(machine);

    MachineDto machineDto = getMachineDto();
    MachineDto machineCreated = machineService.createMachine(machineDto);

    assertEquals(machineDto, machineCreated);

  }

  @Test
  void givenMachine_whenCreateMachineIsCalled_thenReturnMachineIdentifierAlreadyInUse() {
    MachineDto machineDto = getMachineDto();
    Machine machine = getMockMachine();

    when(machineRepository.findMachineByIdentifier(any())).thenReturn(Optional.of(machine));

    MachineIdentifierAlreadyInUseException exception = assertThrows(
        MachineIdentifierAlreadyInUseException.class, () -> {
          machineService.createMachine(machineDto);
        });
    assertEquals("Machine identifier already in use", exception.getMessage());
  }

  @Test
  void givenMachineExists_whenGetMachineByIdIsCalled_thenReturnMachineDto() {
    Machine mockMachine = getMockMachine();
    when(machineRepository.findById(any())).thenReturn(Optional.of(mockMachine));

    MachineDto machineDto = getMachineDto();
    MachineDto machineFound = machineService.getMachineById(1L);

    assertEquals(machineDto, machineFound);
  }

  @Test
  void givenMachineDoesNotExist_whenGetMachineByIdIsCalled_thenThrowMachineNotFoundException() {
    when(machineRepository.findById(any())).thenReturn(Optional.empty());
    assertThrows(MachineNotFoundException.class, () -> {
      machineService.getMachineById(1L);
    });

  }

  @Test
  void givenMachine_whenUpdateMachineIsCalled_thenUpdateMachine() {
    Machine machine = getMockMachine();
    when(machineRepository.existsById(any())).thenReturn(true);
    when(machineRepository.save(any(Machine.class))).thenReturn(machine);

    MachineDto machineDto = getMachineDto();
    MachineDto machineUpdated = machineService.updateMachine(machineDto);

    assertEquals(machineDto, machineUpdated);
  }

  @Test
  void givenMachineDoesNotExist_whenUpdateMachineIsCalled_thenThrowMachineNotFoundException() {
    when(machineRepository.existsById(any())).thenReturn(false);
    assertThrows(MachineNotFoundException.class, () -> {
      machineService.updateMachine(getMachineDto());
    });
    verify(machineRepository, never()).save(any());
  }

  @Test
  void givenMachine_whenDeleteMachineIsCalled_thenShouldDeleteMachine() {
    Long id = 1L;
    when(machineRepository.existsById(any())).thenReturn(true);
    machineService.deleteMachine(id);
    verify(machineRepository, times(1)).deleteById(id);
  }

  @Test
  void givenMachineDoesNotExist_whenDeleteMachineIsCalled_thenThrowMachineNotFoundException() {
    Long id = 1L;
    when(machineRepository.existsById(any())).thenReturn(false);
    assertThrows(MachineNotFoundException.class, () -> {
      machineService.deleteMachine(id);
    });
    verify(machineRepository, never()).deleteById(id);
  }

  @Test
  void givenDefaultPagination_whenGetAllMachinesIsCalled_thenReturnAllMachines() {
    Machine mockMachine = getMockMachine();
    List<Machine> machines = List.of(mockMachine);
    Page<Machine> machinePage = new PageImpl<>(machines);
    PaginationRequestDto defaultPagination = PaginationRequestDto.builder()
        .page(1)
        .size(10)
        .sortBy("id")
        .direction("DESC")
        .build();

    when(machineRepository.findAll(any(Pageable.class))).thenReturn(machinePage);
    machineService.getAllMachines(defaultPagination);
    verify(machineRepository, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void givenMachineExists_whenGetMachineByIdentifierIsCalled_thenReturnMachineDto() {
    Machine mockMachine = getMockMachine();
    when(machineRepository.findMachineByIdentifier(any())).thenReturn(Optional.of(mockMachine));

    MachineDto machineDto = getMachineDto();
    MachineDto machineFound = machineService.getMachineByIdentifier("Washing Machine");

    assertEquals(machineDto, machineFound);
  }

  @Test
  void givenMachineDoesNotExist_whenGetMachineByIdentifierIsCalled_thenThrowMachineNotFoundException() {
    when(machineRepository.findMachineByIdentifier(any())).thenReturn(Optional.empty());
    assertThrows(MachineNotFoundException.class, () -> {
      machineService.getMachineByIdentifier("Washing Machine");
    });
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
