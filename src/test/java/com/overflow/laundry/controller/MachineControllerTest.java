package com.overflow.laundry.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.overflow.laundry.constant.ObjectValidatorErrors;
import com.overflow.laundry.exception.MachineIdentifierAlreadyInUseException;
import com.overflow.laundry.exception.MachineNotFoundException;
import com.overflow.laundry.model.dto.MachineDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.model.dto.PaginationResponseDto;
import com.overflow.laundry.service.MachineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MachineController.class)
public class MachineControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MachineService machineService;

  @Test
  void givenMockedMachine_whenMachineIsCreated_thenReturnCreated() throws Exception {

    MachineDto mockMachine = getMachineDto(null, "Washer 1", "Condominium 1", "Washer");

    when(machineService.createMachine(any(MachineDto.class))).thenReturn(mockMachine);

    String machineJson = objectMapper.writeValueAsString(mockMachine);
    mockMvc.perform(post("/machines")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isCreated());
  }

  @ParameterizedTest
  @MethodSource("provideStringsForIsNull")
  void givenMachineWithNullValue_whenMachineIsCreated_thenReturnBadRequest(String identifier,
                                                                           String condominium,
                                                                           String type) throws Exception {
    MachineDto mockMachine = getMachineDto(null, identifier, condominium, type);

    String machineJson = objectMapper.writeValueAsString(mockMachine);
    mockMvc.perform(post("/machines")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @MethodSource("provideStringsForIsEmpty")
  void givenMachineWithEmptyValue_whenMachineIsCreated_thenReturnBadRequest(String identifier,
                                                                            String condominium,
                                                                            String type) throws Exception {
    MachineDto mockMachine = getMachineDto(null, identifier, condominium, type);

    String machineJson = objectMapper.writeValueAsString(mockMachine);
    mockMvc.perform(post("/machines")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void givenMachineExists_whenGetMachineById_thenReturnMachine() throws Exception {
    MachineDto mockMachine = getMachineDto(1L, "1", "Condominium 1", "Washer");

    when(machineService.getMachineById(any())).thenReturn(mockMachine);

    mockMvc.perform(get("/machines/1")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void givenMachineDoesNotExist_whenGetMachineById_thenReturnMachineNotFoundException() throws Exception {

    when(machineService.getMachineById(any())).thenThrow(MachineNotFoundException.class);
    mockMvc.perform(get("/machines/1")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void givenInvalidMachineId_whenGetMachineById_thenReturnBadRequest() throws Exception {

    mockMvc.perform(get("/machines/foo")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void givenMachineExists_whenGetMachineByIdentifier_thenReturnMachine() throws Exception {
    MachineDto mockMachine = getMachineDto(1L, "machine-identifier", "Condominium 1", "Washer");

    when(machineService.getMachineByIdentifier(any())).thenReturn(mockMachine);

    mockMvc.perform(get("/machines/identifier/machine-identifier")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.identifier").value("machine-identifier"));
  }

  @Test
  void givenMachineDoesNotExists_whenGetMachineByIdentifier_thenReturnMachineNotFoundException() throws Exception {

    String expectedMessage = "Machine not found with the provided identifier";
    when(machineService.getMachineByIdentifier(any())).thenThrow(
        new MachineNotFoundException(expectedMessage));
    mockMvc.perform(get("/machines/identifier/machine-identifier")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Machine not found"))
        .andExpect(jsonPath("$.data.details").value(expectedMessage));
  }

  @Test
  void givenIdentifierIsEmpty_whenGetMachineByIdentifier_thenReturnBadRequest() throws Exception {

    mockMvc.perform(get("/machines/identifier/   ")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isBadRequest());

  }

  @Test
  void givenIdentifierIsNull_whenGetMachineByIdentifier_thenReturnNotFound() throws Exception {

    mockMvc.perform(get("/machines/identifier/")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isNotFound());

  }

  @Test
  void givenMachine_whenMachineIsUpdated_thenReturnAccepted() throws Exception {
    MachineDto mockMachine = getMachineDto(1L, "1", "Condominium 1", "Washer");

    when(machineService.updateMachine(any(MachineDto.class))).thenReturn(mockMachine);

    String machineJson = objectMapper.writeValueAsString(mockMachine);
    mockMvc.perform(put("/machines")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isAccepted());
  }

  @Test
  void givenMachine_whenMachineIsDeleted_thenShouldDeleteMachine() throws Exception {

    doNothing().when(machineService).deleteMachine(1L);
    mockMvc.perform(delete("/machines/1")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isNoContent());
  }


  @Test
  void givenDefaultPagination_whenGetAllMachinesIsCalled_thenReturnEmptyList() throws Exception {

    PaginationResponseDto<MachineDto> mockPaginationResponse = PaginationResponseDto.<MachineDto>builder()
        .content(List.of())
        .totalPages(0)
        .totalElements(0)
        .size(10)
        .page(0)
        .empty(true)
        .last(true)
        .first(true)
        .build();

    when(machineService.getAllMachines(any())).thenReturn(mockPaginationResponse);
    mockMvc.perform(get("/machines")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content").isEmpty());
  }

  @Test
  void givenDefaultPagination_whenGetAllMachinesIsCalled_thenReturnListOfMachines() throws Exception {

    PaginationResponseDto<MachineDto> mockPaginationResponse = getMachineDtoPaginationResponseDto();

    when(machineService.getAllMachines(any(PaginationRequestDto.class)))
        .thenReturn(mockPaginationResponse);

    mockMvc.perform(get("/machines")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content[0].id").value(1))
        .andExpect(jsonPath("$.data.content[1].id").value(2));
  }

  @Test
  void givenPaginationInfoIsProvided_whenGetAllMachinesIsCalled_thenReturnListOfMachines() throws Exception {

    PaginationResponseDto<MachineDto> mockPaginationResponse = getMachineDtoPaginationResponseDto();
    PaginationRequestDto paginationRequestDto = PaginationRequestDto.builder()
        .page(1)
        .size(10)
        .sortBy("id")
        .direction("DESC")
        .build();


    when(machineService.getAllMachines(paginationRequestDto))
        .thenReturn(mockPaginationResponse);

    mockMvc.perform(get("/machines?page=" + paginationRequestDto.page()
            + "&size=" + paginationRequestDto.size()
            + "&sortBy=" + paginationRequestDto.sortBy()
            + "&direction=" + paginationRequestDto.direction())
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content[0].id").value(1))
        .andExpect(jsonPath("$.data.content[1].id").value(2));
  }

  @ParameterizedTest
  @MethodSource("provideBrokenPaginationInfo")
  void givenSomePaginationPropertyIsInvalid_whenGetAllMachinesIsCalled_thenReturnBadRequest(Integer size,
                                                                                            Integer page,
                                                                                            String sortBy,
                                                                                            String direction,
                                                                                            String expectedMessage)
      throws Exception {

    mockMvc.perform(get("/machines?page=" + size
            + "&size=" + page
            + "&sortBy=" + sortBy
            + "&direction=" + direction)
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid parameter"))
        .andExpect(jsonPath("$.data.details").value(expectedMessage));
  }

  @Test
  void givenDefaultPagination_whenGetAllMachinesIsCalled_thenReturnStandardJsonFormat() throws Exception {

    PaginationResponseDto<MachineDto> mockPaginationResponse = getMachineDtoPaginationResponseDto();

    when(machineService.getAllMachines(any(PaginationRequestDto.class)))
        .thenReturn(mockPaginationResponse);

    mockMvc.perform(get("/machines")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").isBoolean())
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.data.content[0].id").value(1))
        .andExpect(jsonPath("$.data.content[1].id").value(2))
        .andExpect(jsonPath("$.data.totalPages").isNumber())
        .andExpect(jsonPath("$.data.totalElements").isNumber())
        .andExpect(jsonPath("$.data.size").isNumber())
        .andExpect(jsonPath("$.data.page").isNumber())
        .andExpect(jsonPath("$.data.empty").isBoolean())
        .andExpect(jsonPath("$.data.last").isBoolean())
        .andExpect(jsonPath("$.data.first").isBoolean())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void givenNothing_whenCallingMachineEndpoint_thenThrowInternalServerError() throws Exception {

    when(machineService.getMachineById(any())).thenThrow(RuntimeException.class);
    mockMvc.perform(get("/machines/1")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isInternalServerError());
  }

  @Test
  void givenMachine_whenMachineIsCreated_thenReturnMachineIdentifierAlreadyInUse() throws Exception {
    MachineDto mockMachine = getMachineDto(null, "Washer 1", "Condominium 1", "Washer");

    when(machineService.createMachine(any(MachineDto.class))).thenThrow(new MachineIdentifierAlreadyInUseException(
        "Machine identifier already in use"));

    String machineJson = objectMapper.writeValueAsString(mockMachine);
    mockMvc.perform(post("/machines")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isConflict());

  }

  private static MachineDto getMachineDto(Long id, String identifier, String condominium, String type) {
    return MachineDto.builder()
        .id(id)
        .identifier(identifier)
        .condominium(condominium)
        .type(type)
        .build();
  }

  private static PaginationResponseDto<MachineDto> getMachineDtoPaginationResponseDto() {
    MachineDto mockMachineDto = getMachineDto(1L, "1", "Condominium 1", "Washer");
    MachineDto mockMachineDto2 = getMachineDto(2L, "2", "Condominium 2", "Washer");
    return PaginationResponseDto.<MachineDto>builder()
        .content(List.of(mockMachineDto, mockMachineDto2))
        .totalPages(1)
        .totalElements(2)
        .size(10)
        .page(0)
        .empty(false)
        .last(true)
        .first(true)
        .build();
  }

  private static Stream<Arguments> provideStringsForIsNull() {
    return Stream.of(
        Arguments.of(null, "central", "Washer"),
        Arguments.of("123", null, "Washer"),
        Arguments.of("123", "central", null)
    );
  }

  private static Stream<Arguments> provideStringsForIsEmpty() {
    return Stream.of(
        Arguments.of("", "central", "Washer"),
        Arguments.of("123", "", "Washer"),
        Arguments.of("123", "central", "")
    );
  }

  public static Stream<Arguments> provideBrokenPaginationInfo() {
    return Stream.of(
        Arguments.of(1, 10, "id", "INVALID", ObjectValidatorErrors.PAGINATION_DIRECTION_FORMAT_INVALID),
        Arguments.of(0, 10, "id", "ASC", ObjectValidatorErrors.PAGINATION_PAGE_INVALID),
        Arguments.of(1, -1, "id", "ASC", ObjectValidatorErrors.PAGINATION_SIZE_INVALID)
    );
  }
}
