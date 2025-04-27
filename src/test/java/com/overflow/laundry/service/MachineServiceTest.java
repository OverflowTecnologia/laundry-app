package com.overflow.laundry.service;

import com.overflow.laundry.exception.MachineIdentifierAlreadyInUseException;
import com.overflow.laundry.exception.MachineNotFoundException;
import com.overflow.laundry.model.Condominium;
import com.overflow.laundry.model.Machine;
import com.overflow.laundry.model.dto.CondominiumResponseDto;
import com.overflow.laundry.model.dto.MachineRequestDto;
import com.overflow.laundry.model.dto.MachineResponseDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.model.mapper.CondominiumMapper;
import com.overflow.laundry.model.mapper.MachineMapper;
import com.overflow.laundry.repository.CondominiumRepository;
import com.overflow.laundry.repository.MachineRepository;
import com.overflow.laundry.service.impl.MachineServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

  @Mock
  CondominiumRepository condominiumRepository;

  @Mock
  CondominiumMapper condominiumMapper;

  @BeforeEach
  void setUp() {
    machineService = new MachineServiceImpl(machineRepository, machineMapper, condominiumRepository);
  }

  @Test
  void givenMachine_whenCreateMachineIsCalled_thenCreateMachine() {

    Machine machine = getMockMachine();
    when(condominiumMapper.toDto(any())).thenReturn(getMockCondominiumDto());
    when(condominiumRepository.findById(any())).thenReturn(Optional.of(getMockCondominium()));
    when(machineRepository.save(any(Machine.class))).thenReturn(machine);

    MachineRequestDto machineRequestDto = getMachineRequestDto();
    MachineResponseDto machineCreated = machineService.createMachine(machineRequestDto);

    assertNotNull(machineCreated.id());
    assertEquals(machineRequestDto.identifier(), machineCreated.identifier());
    assertEquals(machineRequestDto.condominiumId(), machineCreated.condominium().id());
    assertEquals(machineRequestDto.type(), machineCreated.type());

  }

  @Test
  void givenMachine_whenCreateMachineIsCalled_thenReturnMachineIdentifierAlreadyInUse() {
    MachineRequestDto machineRequestDto = getMachineRequestDto();
    Machine machine = getMockMachine();

    when(machineRepository.findMachineByCondominiumIdAndIdentifier(any(), any())).thenReturn(Optional.of(machine));
    when(condominiumRepository.findById(any())).thenReturn(Optional.of(getMockCondominium()));

    MachineIdentifierAlreadyInUseException exception = assertThrows(
        MachineIdentifierAlreadyInUseException.class, () -> {
          machineService.createMachine(machineRequestDto);
        });
    assertEquals("Machine identifier already in use", exception.getMessage());
  }

  @Test
  void givenMachineWithId_whenCreateMachineIsCalled_thenThrowIllegalArgumentException() {
    MachineRequestDto machineRequestDto = MachineRequestDto.builder()
        .id(1L)
        .identifier("Washing Machine")
        .condominiumId(1L)
        .type("Washer")
        .build();

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class, () -> {
          machineService.createMachine(machineRequestDto);
        });
    assertEquals("Machine ID should NOT be provided for creation", exception.getMessage());
  }

  @Test
  void givenMachineExists_whenGetMachineByIdIsCalled_thenReturnMachineDto() {
    Machine mockMachine = getMockMachine();

    when(condominiumMapper.toDto(any())).thenReturn(getMockCondominiumDto());
    when(machineRepository.findById(any())).thenReturn(Optional.of(mockMachine));

    MachineResponseDto mockMachineResponseDto = getMachineResponseDto();
    MachineResponseDto machineFound = machineService.getMachineById(1L);

    assertEquals(mockMachineResponseDto, machineFound);
  }

  @Test
  void givenMachineDoesNotExist_whenGetMachineByIdIsCalled_thenThrowMachineNotFoundException() {
    when(machineRepository.findById(any())).thenReturn(Optional.empty());
    MachineNotFoundException exception = assertThrows(MachineNotFoundException.class, () -> {
      machineService.getMachineById(1L);
    });
    assertEquals("Machine not found", exception.getMessage());

  }

  @Test
  void givenMachine_whenUpdateMachineIsCalled_thenUpdateMachine() {
    Machine machine = getMockMachine();
    when(machineRepository.existsById(any())).thenReturn(true);
    when(condominiumMapper.toDto(any())).thenReturn(getMockCondominiumDto());
    when(condominiumRepository.findById(any())).thenReturn(Optional.of(getMockCondominium()));
    when(machineRepository.save(any(Machine.class))).thenReturn(machine);

    MachineRequestDto machineRequestDto = MachineRequestDto.builder()
        .id(1L)
        .identifier("Washing Machine")
        .condominiumId(1L)
        .type("Washer")
        .build();
    MachineResponseDto machineUpdated = machineService.updateMachine(machineRequestDto);

    assertEquals(machineRequestDto.id(), machineUpdated.id());
    assertEquals(machineRequestDto.identifier(), machineUpdated.identifier());
    assertEquals(machineRequestDto.condominiumId(), machineUpdated.condominium().id());
    assertEquals(machineRequestDto.type(), machineUpdated.type());
  }

  @Test
  void givenMachineDoesNotExist_whenUpdateMachineIsCalled_thenThrowMachineNotFoundException() {
    when(machineRepository.existsById(any())).thenReturn(false);
    MachineNotFoundException exception = assertThrows(MachineNotFoundException.class, () -> {
      machineService.updateMachine(getMachineRequestDto());
    });
    verify(machineRepository, never()).save(any());
    assertEquals("Machine not found", exception.getMessage());
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
    MachineNotFoundException exception = assertThrows(MachineNotFoundException.class, () -> {
      machineService.deleteMachine(id);
    });
    verify(machineRepository, never()).deleteById(id);
    assertEquals("Machine not found", exception.getMessage());
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
    //TODO: Refactor test
  }

  @Test
  void givenMachineExists_whenGetMachineByIdentifierIsCalled_thenReturnMachineDtoCondominiumAnd() {
    Machine mockMachine = getMockMachine();
    when(condominiumMapper.toDto(any())).thenReturn(getMockCondominiumDto());
    when(machineRepository.findMachineByCondominiumIdAndIdentifier(any(), any())).thenReturn(Optional.of(mockMachine));

    MachineResponseDto mockMachineResponseDto = getMachineResponseDto();
    MachineResponseDto machineFound = machineService.getMachineByCondominiumAndIdentifier(
        mockMachine.getCondominium().getId(), "Washing Machine");

    assertEquals(mockMachineResponseDto, machineFound);
  }

  @Test
  void givenMachineDoesNotExist_whenGetMachineByIdentifierIsCalled_thenThrowMachineNotFoundExceptionCondomiunAnd() {
    when(machineRepository.findMachineByCondominiumIdAndIdentifier(any(), any())).thenReturn(Optional.empty());
    MachineNotFoundException exception = assertThrows(MachineNotFoundException.class, () -> {
      machineService.getMachineByCondominiumAndIdentifier(1L, "Washing Machine");
    });
    assertEquals("Machine not found", exception.getMessage());
  }

  private static Machine getMockMachine() {
    return new Machine(1L,
        "Washing Machine",
        "Washer",
        getMockCondominium());
  }

  private static MachineRequestDto getMachineRequestDto() {
    return MachineRequestDto.builder()
        .identifier("Washing Machine")
        .condominiumId(1L)
        .type("Washer")
        .build();
  }

  private static MachineResponseDto getMachineResponseDto() {
    return MachineResponseDto.builder()
        .id(1L)
        .identifier("Washing Machine")
        .condominium(getMockCondominiumDto())
        .type("Washer")
        .build();
  }

  private static Condominium getMockCondominium() {
    return new Condominium(1L, "Condominium 1", "123 Main St", "123456789", "test@test.com", null);

  }

  private static CondominiumResponseDto getMockCondominiumDto() {
    return CondominiumResponseDto.builder()
        .id(1L)
        .name("Condominium 1")
        .email("test@test.com")
        .address("123 Main St")
        .contactPhone("123456789")
        .build();
  }
}
