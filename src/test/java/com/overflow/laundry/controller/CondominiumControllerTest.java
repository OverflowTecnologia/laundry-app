package com.overflow.laundry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.overflow.laundry.exception.CondominiumNotFoundException;
import com.overflow.laundry.model.dto.CondominiumRequestDto;
import com.overflow.laundry.model.dto.CondominiumResponseDto;
import com.overflow.laundry.service.CondominiumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.overflow.laundry.constant.MessageResponseEnum.CONDOMINIUM_NOT_FOUND;
import static com.overflow.laundry.constant.ObjectValidatorErrors.CONDOMINIUM_ADDRESS_NOT_EMPTY_NULL;
import static com.overflow.laundry.constant.ObjectValidatorErrors.CONDOMINIUM_CONTACT_NOT_EMPTY_NULL;
import static com.overflow.laundry.constant.ObjectValidatorErrors.CONDOMINIUM_EMAIL_FORMAT_NOT_VALID;
import static com.overflow.laundry.constant.ObjectValidatorErrors.CONDOMINIUM_EMAIL_NOT_EMPTY_NULL;
import static com.overflow.laundry.constant.ObjectValidatorErrors.CONDOMINIUM_NAME_NOT_EMPTY_NULL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CondominiumController.class)
public class CondominiumControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  JwtDecoder jwtDecoder;

  @MockitoBean
  private CondominiumService condominiumService;

  @BeforeEach
  void setUpTestToken() {
    mockTestJwtToken();
  }

  @Test
  void givenCondominium_whenCreateCondominiumIsCalled_thenReturnCreated() throws Exception {

    CondominiumRequestDto condominiumRequestDto = getMockCondominiumDto();
    CondominiumResponseDto mockCondominiumRequestDto = getMockCondominiumResponseDto();

    when(condominiumService.createCondominium(condominiumRequestDto)).thenReturn(mockCondominiumRequestDto);
    String condominiumJson = objectMapper.writeValueAsString(condominiumRequestDto);

    mockMvc.perform(post("/condominiums")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json")
            .content(condominiumJson))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.id").value(1L))
        .andExpect(jsonPath("$.data.name").value("Test Condominium"))
        .andExpect(jsonPath("$.data.address").value("123 Test St"))
        .andExpect(jsonPath("$.data.contactPhone").value("1234567890"))
        .andExpect(jsonPath("$.data.email").value("john@john.com"));
  }

  @Test
  void givenCondominium_whenCreateCondominiumIsCalled_thenReturnStandardResponseJson() throws Exception {
    CondominiumRequestDto condominiumRequestDto = getMockCondominiumDto();
    CondominiumResponseDto mockCondominiumResponseDto = getMockCondominiumResponseDto();

    when(condominiumService.createCondominium(condominiumRequestDto)).thenReturn(mockCondominiumResponseDto);
    String condominiumJson = objectMapper.writeValueAsString(condominiumRequestDto);

    mockMvc.perform(post("/condominiums")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json")
            .content(condominiumJson))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").isNotEmpty())
        .andExpect(jsonPath("$.data.id").value(1L))
        .andExpect(jsonPath("$.data.name").value("Test Condominium"))
        .andExpect(jsonPath("$.data.address").value("123 Test St"))
        .andExpect(jsonPath("$.data.contactPhone").value("1234567890"))
        .andExpect(jsonPath("$.data.email").value("john@john.com"))
        .andExpect(jsonPath("$.timestamp").isNotEmpty());

  }

  @ParameterizedTest
  @MethodSource("provideInvalidCondominiumData")
  void givenSomeCondominiumInfoIsMissing_whenCreateCondominiumIsCalled_thenReturnBadRequest(
      CondominiumRequestDto testCondominium, String expectedErrorMessage) throws Exception {

    String condominiumJson = objectMapper.writeValueAsString(testCondominium);

    mockMvc.perform(post("/condominiums")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json")
            .content(condominiumJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
        .andExpect(jsonPath("$.data.details").value(expectedErrorMessage));
  }

  @Test
  void givenCondominiumExists_whenGetCondominiumById_thenReturnCondominium() throws Exception {
    when(condominiumService.getCondominiumById(any())).thenReturn(getMockCondominiumResponseDto());

    mockMvc.perform(get("/condominiums/1")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1L))
        .andExpect(jsonPath("$.data.name").value("Test Condominium"))
        .andExpect(jsonPath("$.data.address").value("123 Test St"))
        .andExpect(jsonPath("$.data.contactPhone").value("1234567890"))
        .andExpect(jsonPath("$.data.email").value("john@john.com"));

  }

  @Test
  void givenWrongDataType_whenGetCondominiumById_thenReturnBadRequest() throws Exception {

    mockMvc.perform(get("/condominiums/a")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Bad Request"))
        .andExpect(jsonPath("$.data.details").value("id should be of type java.lang.Long"));
  }

  @Test
  void givenCondominiumId_whenGetCondominiumById_thenReturnNotFound() throws Exception {
    when(condominiumService.getCondominiumById(any())).thenThrow(new
        CondominiumNotFoundException(CONDOMINIUM_NOT_FOUND.label));

    mockMvc.perform(get("/condominiums/55")
            .header("Authorization", "Bearer test_token")
            .contentType("application/json"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Not Found"))
        .andExpect(jsonPath("$.data.details").value(CONDOMINIUM_NOT_FOUND.label));
  }

  private static CondominiumRequestDto getMockCondominiumDto() {
    return CondominiumRequestDto.builder()
        .name("Test Condominium")
        .address("123 Test St")
        .contactPhone("1234567890")
        .email("john@john.com")
        .build();
  }

  public static Stream<Arguments> provideInvalidCondominiumData() {
    CondominiumRequestDto condominiumRequestDtoEmailTest =
        createCondominiumRequestDto("Jr", "123 Test St", "1234567890", null);
    CondominiumRequestDto condominiumRequestDtoEmailFormatTest =
        createCondominiumRequestDto("Jr", "123 Test St", "1234567890", "john.john.com");
    CondominiumRequestDto condominiumRequestDtoContactTest =
        createCondominiumRequestDto("Jr", "123 Test St", null, "john@john.com");
    CondominiumRequestDto condominiumRequestDtoAddressTest =
        createCondominiumRequestDto("Jr", null, "1234567890", "john@john.com");
    CondominiumRequestDto condominiumRequestDtoNameTest =
        createCondominiumRequestDto(null, "123 Test St", "1234567890", "john@john.com");

    return Stream.of(
        Arguments.of(condominiumRequestDtoEmailTest, CONDOMINIUM_EMAIL_NOT_EMPTY_NULL),
        Arguments.of(condominiumRequestDtoEmailFormatTest, CONDOMINIUM_EMAIL_FORMAT_NOT_VALID),
        Arguments.of(condominiumRequestDtoContactTest, CONDOMINIUM_CONTACT_NOT_EMPTY_NULL),
        Arguments.of(condominiumRequestDtoAddressTest, CONDOMINIUM_ADDRESS_NOT_EMPTY_NULL),
        Arguments.of(condominiumRequestDtoNameTest, CONDOMINIUM_NAME_NOT_EMPTY_NULL)

    );
  }

  private static CondominiumRequestDto createCondominiumRequestDto(String name, String address,
                                                                   String contactPhone, String email) {
    return CondominiumRequestDto.builder()
        .name(name)
        .address(address)
        .contactPhone(contactPhone)
        .email(email)
        .build();
  }

  private static CondominiumResponseDto getMockCondominiumResponseDto() {
    return CondominiumResponseDto.builder()
        .id(1L)
        .name("Test Condominium")
        .address("123 Test St")
        .contactPhone("1234567890")
        .email("john@john.com")
        .build();
  }


  private void mockTestJwtToken() {
    Jwt mockjwt = Jwt.withTokenValue("test_token")
        .header("alg", "none")
        .claim("sub", "test_user")
        .build();
    when(jwtDecoder.decode("test_token")).thenReturn(mockjwt);
  }
}
