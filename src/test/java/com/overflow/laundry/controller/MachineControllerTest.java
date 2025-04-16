package com.overflow.laundry.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.overflow.laundry.constant.ObjectValidatorErrors;
import com.overflow.laundry.exception.MachineIdentifierAlreadyInUseException;
import com.overflow.laundry.exception.MachineNotFoundException;
import com.overflow.laundry.model.dto.CondominiumDto;
import com.overflow.laundry.model.dto.MachineRequestDto;
import com.overflow.laundry.model.dto.MachineResponseDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.model.dto.PaginationResponseDto;
import com.overflow.laundry.service.MachineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
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

  @MockitoBean
  JwtDecoder jwtDecoder;

  @BeforeEach
  void setUpTestToken() {
    mockTestJwtToken();
  }


  @Test
  void givenMockedMachine_whenMachineIsCreated_thenReturnCreated() throws Exception {

    MachineResponseDto mockMachineResponse = getMachineResponseDto(1L, "Washer 1", getMockCondominium(), "Washer");
    when(machineService.createMachine(any(MachineRequestDto.class))).thenReturn(mockMachineResponse);
    MachineRequestDto mockMachineRequestDto = getMachineRequestDto();

    String machineJson = objectMapper.writeValueAsString(mockMachineRequestDto);
    mockMvc.perform(post("/machines")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isCreated());
  }


  @ParameterizedTest
  @MethodSource("provideMachinesDtoWithMissingValues")
  void givenMachineWithNullValue_whenMachineIsCreated_thenReturnBadRequest(MachineRequestDto mockMachine)
      throws Exception {
    String machineJson = objectMapper.writeValueAsString(mockMachine);
    mockMvc.perform(post("/machines")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }


  @Test
  void givenMachineExists_whenGetMachineById_thenReturnMachine() throws Exception {
    MachineResponseDto mockMachine = getMachineResponseDto(1L, "1", getMockCondominium(), "Washer");

    when(machineService.getMachineById(any())).thenReturn(mockMachine);

    mockMvc.perform(get("/machines/1")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void givenMachineDoesNotExist_whenGetMachineById_thenReturnMachineNotFoundException() throws Exception {

    when(machineService.getMachineById(any())).thenThrow(MachineNotFoundException.class);
    mockMvc.perform(get("/machines/1")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void givenInvalidMachineId_whenGetMachineById_thenReturnBadRequest() throws Exception {

    mockMvc.perform(get("/machines/foo")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void givenMachineExists_whenGetMachineByIdentifier_thenReturnMachine() throws Exception {
    String machineIdentifier = "machine-identifier";
    MachineResponseDto mockMachine = getMachineResponseDto(1L, machineIdentifier, getMockCondominium(), "Washer");

    when(machineService.getMachineByIdentifier(any())).thenReturn(mockMachine);

    mockMvc.perform(get("/machines/identifier/" + machineIdentifier)
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.identifier").value(machineIdentifier));
  }

  @Test
  void givenMachineDoesNotExists_whenGetMachineByIdentifier_thenReturnMachineNotFoundException() throws Exception {

    String expectedMessage = "Machine not found with the provided identifier";
    when(machineService.getMachineByIdentifier(any())).thenThrow(
        new MachineNotFoundException(expectedMessage));
    mockMvc.perform(get("/machines/identifier/machine-identifier")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Not Found"))
        .andExpect(jsonPath("$.data.details").value(expectedMessage));
  }

  @Test
  void givenIdentifierIsEmpty_whenGetMachineByIdentifier_thenReturnBadRequest() throws Exception {

    mockMvc.perform(get("/machines/identifier/   ")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isBadRequest());

  }

  @Test
  void givenIdentifierIsNull_whenGetMachineByIdentifier_thenReturnNotFound() throws Exception {

    mockMvc.perform(get("/machines/identifier/")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isNotFound());

  }

  @Test
  void givenMachine_whenMachineIsUpdated_thenReturnAccepted() throws Exception {
    MachineResponseDto mockMachineResponse = getMachineResponseDto(1L, "identifier 1", getMockCondominium(), "Washer");
    when(machineService.updateMachine(any(MachineRequestDto.class))).thenReturn(mockMachineResponse);

    MachineRequestDto mockMachineRequestDto = MachineRequestDto.builder()
        .id(1L)
        .identifier("identifier 1")
        .condominiumId(1L)
        .type("Washer")
        .build();

    String machineJson = objectMapper.writeValueAsString(mockMachineRequestDto);
    mockMvc.perform(put("/machines")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isAccepted());
  }


  @Test
  void givenMachine_whenMachineIsDeleted_thenShouldDeleteMachine() throws Exception {

    doNothing().when(machineService).deleteMachine(1L);
    mockMvc.perform(delete("/machines/1")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isNoContent());
  }

  @Test
  void givenDefaultPagination_whenGetAllMachinesIsCalled_thenReturnEmptyList() throws Exception {

    PaginationResponseDto<MachineResponseDto> mockPaginationResponse = PaginationResponseDto
        .<MachineResponseDto>builder()
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
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content").isEmpty());
  }

  @Test
  void givenDefaultPagination_whenGetAllMachinesIsCalled_thenReturnListOfMachines() throws Exception {

    PaginationResponseDto<MachineResponseDto> mockPaginationResponse = getMachineDtoPaginationResponseDto();

    when(machineService.getAllMachines(any(PaginationRequestDto.class)))
        .thenReturn(mockPaginationResponse);

    mockMvc.perform(get("/machines")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content[0].id").value(1))
        .andExpect(jsonPath("$.data.content[1].id").value(2));
  }

  @Test
  void givenPaginationInfoIsProvided_whenGetAllMachinesIsCalled_thenReturnListOfMachines() throws Exception {

    PaginationResponseDto<MachineResponseDto> mockPaginationResponse = getMachineDtoPaginationResponseDto();
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
            .header("Authorization", "Bearer test_token")
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
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Bad Request"))
        .andExpect(jsonPath("$.data.details").value(expectedMessage));
  }

  @Test
  void givenDefaultPagination_whenGetAllMachinesIsCalled_thenReturnStandardJsonFormat() throws Exception {

    PaginationResponseDto<MachineResponseDto> mockPaginationResponse = getMachineDtoPaginationResponseDto();

    when(machineService.getAllMachines(any(PaginationRequestDto.class)))
        .thenReturn(mockPaginationResponse);

    mockMvc.perform(get("/machines")
            .header("Authorization", "Bearer test_token")
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
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isInternalServerError());
  }

  @Test
  void givenMachine_whenMachineIsCreated_thenReturnMachineIdentifierAlreadyInUse() throws Exception {
    MachineRequestDto mockMachine = getMachineRequestDto();

    when(machineService.createMachine(any(MachineRequestDto.class))).thenThrow(
        new MachineIdentifierAlreadyInUseException(
        "Machine identifier already in use"));

    String machineJson = objectMapper.writeValueAsString(mockMachine);
    mockMvc.perform(post("/machines")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json")
            .content(machineJson))
        .andDo(print())
        .andExpect(status().isConflict());
  }

  private static MachineRequestDto getMachineRequestDto() {
    return MachineRequestDto.builder()
        .identifier("Washer 1")
        .condominiumId(1L)
        .type("Washer")
        .build();
  }

  private static MachineResponseDto getMachineResponseDto(Long id, String identifier,
                                                          CondominiumDto condominium, String type) {
    return MachineResponseDto.builder()
        .id(id)
        .identifier(identifier)
        .condominium(condominium)
        .type(type)
        .build();
  }

  private static PaginationResponseDto<MachineResponseDto> getMachineDtoPaginationResponseDto() {
    MachineResponseDto mockMachineDto = getMachineResponseDto(1L, "1", getMockCondominium(), "Washer");
    MachineResponseDto mockMachineDto2 = getMachineResponseDto(2L, "2", getMockCondominium(), "Dryer");
    return PaginationResponseDto.<MachineResponseDto>builder()
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

  private static CondominiumDto getMockCondominium() {
    return CondominiumDto.builder()
        .id(1L)
        .name("Condominium 1")
        .email("test@test.com")
        .address("123 Main St")
        .contactPhone("123456789")
        .build();
  }

  private static Stream<Arguments> provideMachinesDtoWithMissingValues() {
    MachineRequestDto.builder().identifier("identifier").condominiumId(1L).type("Washer").build();
    return Stream.of(
        Arguments.of(MachineRequestDto.builder().identifier(null).condominiumId(1L).type("Washer").build()),
        Arguments.of(MachineRequestDto.builder().identifier("identifier").condominiumId(null).type("Washer").build()),
        Arguments.of(MachineRequestDto.builder().identifier("identifier").condominiumId(1L).type(null).build())
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

  private void mockTestJwtToken() {
    Jwt mockjwt = Jwt.withTokenValue("test_token")
        .header("alg", "none")
        .claim("sub", "test_user")
        .build();
    when(jwtDecoder.decode("test_token")).thenReturn(mockjwt);
  }
}
