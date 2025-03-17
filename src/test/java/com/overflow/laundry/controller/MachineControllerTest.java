package com.overflow.laundry.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.overflow.laundry.constant.ObjectValidatorErrors;
import com.overflow.laundry.exception.MachineNotFoundException;
import com.overflow.laundry.model.dto.MachineDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.service.MachineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
  void should_returnAccepted_whenMachineIsCreated() throws Exception {

    MachineDto mockMachine = getMachineDto(null, "Washer 1", "Condominium 1", "Washer");

    when(machineService.createMachine(any(MachineDto.class))).thenReturn(mockMachine);

    String machineJson = objectMapper.writeValueAsString(mockMachine);
    mockMvc.perform(post("/machine")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isAccepted());
  }

  @ParameterizedTest
  @MethodSource("provideStringsForIsNull")
  void should_returnBadRequest_whenOneOfTheValuesIsNull(String identifier,
                                                        String condominium,
                                                        String type) throws Exception {
    MachineDto mockMachine = getMachineDto(null, identifier, condominium, type);

    String machineJson = objectMapper.writeValueAsString(mockMachine);
    mockMvc.perform(post("/machine")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @MethodSource("provideStringsForIsEmpty")
  void should_returnBadRequest_whenOneOfTheValuesIsEmpty(String identifier,
                                                         String condominium,
                                                         String type) throws Exception {
    MachineDto mockMachine = getMachineDto(null, identifier, condominium, type);

    String machineJson = objectMapper.writeValueAsString(mockMachine);
    mockMvc.perform(post("/machine")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_returnMachine_whenGetMachineById() throws Exception {
    MachineDto mockMachine = getMachineDto(1L, "1", "Condominium 1", "Washer");

    when(machineService.getMachineById(any())).thenReturn(mockMachine);

    String machineJson = objectMapper.writeValueAsString(mockMachine);
    mockMvc.perform(get("/machine/1")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void should_returnMachineNotFoundException_whenGetMachineById() throws Exception {

    when(machineService.getMachineById(any())).thenThrow(MachineNotFoundException.class);
    mockMvc.perform(get("/machine/1")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void should_returnException_whenMachineIdInvalid() throws Exception {

    mockMvc.perform(get("/machine/adas")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_updateMachine_whenMachineIsUpdated() throws Exception {
    MachineDto mockMachine = getMachineDto(1L, "1", "Condominium 1", "Washer");

    when(machineService.updateMachine(any(MachineDto.class))).thenReturn(mockMachine);

    String machineJson = objectMapper.writeValueAsString(mockMachine);
    mockMvc.perform(put("/machine")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isAccepted());
  }

  @Test
  void should_deleteMachine_whenMachineIsDeleted() throws Exception {

    doNothing().when(machineService).deleteMachine(1L);
    mockMvc.perform(delete("/machine/1")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isNoContent());
  }

  private static MachineDto getMachineDto(Long id, String identifier, String condominium,
                                          String type) {
    return MachineDto.builder()
        .id(id)
        .identifier(identifier)
        .condominium(condominium)
        .type(type)
        .build();
  }

  @Test
  void given_defaultPagination_whenGetAllMachinesIsCalled_thenReturnEmptyList() throws Exception {

    Page<MachineDto> page = new PageImpl<>(List.of());
    when(machineService.getAllMachines(any())).thenReturn(page);
    mockMvc.perform(get("/machine")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty());
  }

  @Test
  void given_defaultPagination_whenGetAllMachinesIsCalled_thenReturnListOfMachines() throws Exception {
    MachineDto mockMachine = getMachineDto(1L, "1", "Condominium 1", "Washer");
    MachineDto mockMachine2 = getMachineDto(2L, "2", "Condominium 2", "Washer");

    List<MachineDto> machines = List.of(mockMachine, mockMachine2);
    Page<MachineDto> machinePage = new PageImpl<>(machines);
    when(machineService.getAllMachines(any(PaginationRequestDto.class)))
        .thenReturn(machinePage);

    mockMvc.perform(get("/machine")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[1].id").value(2));
  }

  @Test
  void given_paginationInfoIsProvided_whenGetAllMachinesIsCalled_thenReturnListOfMachines() throws Exception {
    MachineDto mockMachine = getMachineDto(1L, "1", "Condominium 1", "Washer");
    MachineDto mockMachine2 = getMachineDto(2L, "2", "Condominium 2", "Washer");

    PaginationRequestDto paginationRequestDto = PaginationRequestDto.builder()
        .page(0)
        .size(10)
        .sortBy("id")
        .direction("DESC")
        .build();

    List<MachineDto> machines = List.of(mockMachine, mockMachine2);
    Page<MachineDto> machinePage = new PageImpl<>(machines);
    when(machineService.getAllMachines(paginationRequestDto))
        .thenReturn(machinePage);

    mockMvc.perform(get("/machine?page=" + paginationRequestDto.page()
            + "&size=" + paginationRequestDto.size()
            + "&sortBy=" + paginationRequestDto.sortBy()
            + "&direction=" + paginationRequestDto.direction())
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[1].id").value(2));
  }

  @ParameterizedTest
  @MethodSource("provideBrokenPaginationInfo")
  void givenSomePaginationPropertyIsInvalid_whenGetAllMachinesIsCalled_thenReturnBadRequest(Integer size,
                                                                                            Integer page,
                                                                                            String sortBy,
                                                                                            String direction,
                                                                                            String expectedMessage)
      throws Exception {

    mockMvc.perform(get("/machine?page=" + size
            + "&size=" + page
            + "&sortBy=" + sortBy
            + "&direction=" + direction)
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid parameter"))
        .andExpect(jsonPath("$.details").value(expectedMessage));
  }

  @Test
  void givenDefaultPagination_whenGetAllMachinesIsCalled_thenReturnStandardJsonFormat() throws Exception {
    MachineDto mockMachine = getMachineDto(1L, "1", "Condominium 1", "Washer");
    MachineDto mockMachine2 = getMachineDto(2L, "2", "Condominium 2", "Washer");

    List<MachineDto> machines = List.of(mockMachine, mockMachine2);
    Page<MachineDto> machinePage = new PageImpl<>(machines);
    when(machineService.getAllMachines(any(PaginationRequestDto.class)))
        .thenReturn(machinePage);

    mockMvc.perform(get("/machine")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[1].id").value(2))
        .andExpect(jsonPath("$.pageable").exists())
        .andExpect(jsonPath("$.last").isBoolean())
        .andExpect(jsonPath("$.totalPages").isNumber())
        .andExpect(jsonPath("$.totalElements").isNumber())
        .andExpect(jsonPath("$.first").isBoolean())
        .andExpect(jsonPath("$.numberOfElements").isNumber())
        .andExpect(jsonPath("$.size").isNumber())
        .andExpect(jsonPath("$.number").isNumber())
        .andExpect(jsonPath("$.sort").exists())
        .andExpect(jsonPath("$.empty").isBoolean());
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
        Arguments.of(0, 10, "id", "INVALID", ObjectValidatorErrors.PAGINATION_DIRECTION_FORMAT_INVALID),
        Arguments.of(-1, 10, "id", "ASC", ObjectValidatorErrors.PAGINATION_PAGE_INVALID),
        Arguments.of(0, -1, "id", "ASC", ObjectValidatorErrors.PAGINATION_SIZE_INVALID)
    );
  }

}
