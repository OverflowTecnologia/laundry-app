package com.overflow.laundry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.overflow.laundry.config.StandardResponse;
import com.overflow.laundry.constant.ObjectValidatorErrors;
import com.overflow.laundry.exception.StandardErrorMessage;
import com.overflow.laundry.model.dto.MachineRequestDto;
import com.overflow.laundry.model.dto.MachineResponseDto;
import com.overflow.laundry.model.dto.PaginationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/init-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MachineControllerIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @MockitoBean
  JwtDecoder jwtDecoder;

  @BeforeEach
  public void setUp() {
    mockTestJwtTokenWithManagerPermission();
  }

  @Test
  void givenNoToken_whenGetMachineByIdentifier_thenReturnUnauthorized() {
    HttpEntity<Object> requestEntity = new HttpEntity<>(null, new HttpHeaders());
    ResponseEntity<StandardResponse<MachineResponseDto>> response = restTemplate.exchange(
        "http://localhost:" + port + "/machines/identifier",
        GET, requestEntity, new ParameterizedTypeReference<StandardResponse<MachineResponseDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void givenTokenDoesNotHaveManagerPermission_whenGetMachineByIdentifier_thenReturnForbidden() {
    Jwt mockjwt = Jwt.withTokenValue("test_token")
        .header("alg", "none")
        .claim("sub", "test_user")
        .claim("cognito:groups", null)
        .build();
    when(jwtDecoder.decode("test_token")).thenReturn(mockjwt);

    HttpEntity<Object> requestEntityWithDefaultHeaders = createRequestEntityWithDefaultHeaders(null);

    ResponseEntity<StandardResponse<MachineResponseDto>> response = restTemplate.exchange(
        "http://localhost:" + port + "/machines/identifier",
        GET, requestEntityWithDefaultHeaders, new ParameterizedTypeReference<StandardResponse<MachineResponseDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  public void givenMachine_whenCreateMachine_thenReturnMachineCreated() {

    MachineRequestDto machineRequestDto = MachineRequestDto.builder()
        .identifier("identifier")
        .condominiumId(55L)
        .type("test-type")
        .build();

    HttpEntity<MachineRequestDto> requestEntity = createRequestEntityWithDefaultHeaders(machineRequestDto);

    ResponseEntity<StandardResponse<MachineResponseDto>> response = restTemplate.exchange(
        "http://localhost:" + port + "/machines", POST, requestEntity,
        new ParameterizedTypeReference<StandardResponse<MachineResponseDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getData().id()).isNotNull();
    assertThat(response.getBody().getData().identifier()).isEqualTo(machineRequestDto.identifier());
    assertThat(response.getBody().getData().type()).isEqualTo(machineRequestDto.type());
    assertThat(response.getBody().getData().condominium().id()).isEqualTo(machineRequestDto.condominiumId());
  }

  @Test
  void givenIdentifierAndCondominiumId_whenGetMachineByIdentifier_thenReturnMachine() {
    String identifier = "test-identifier";
    Long condominiumId = 55L;
    HttpEntity<Object> requestEntityWithDefaultHeaders = createRequestEntityWithDefaultHeaders(null);

    ResponseEntity<StandardResponse<MachineResponseDto>> response = restTemplate.exchange(
        "http://localhost:" + port + "/machines/identifier?condominiumId=" + condominiumId
            + "&identifier=" + identifier,
        GET, requestEntityWithDefaultHeaders, new ParameterizedTypeReference<StandardResponse<MachineResponseDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getData().identifier()).isEqualTo(identifier);
    assertThat(response.getBody().getData().id()).isNotNull();
    assertThat(response.getBody().getData().type()).isEqualTo("Washer");
    assertThat(response.getBody().getData().condominium().id()).isEqualTo(condominiumId);

  }

  @Test
  void givenIdentifierDoesNotExists_whenGetMachineByIdentifier_thenNotFound() {
    String identifier = "non-existing-identifier";
    String condominiumId = "1";

    HttpEntity<Object> requestEntityWithDefaultHeaders = createRequestEntityWithDefaultHeaders(null);

    ResponseEntity<StandardResponse<MachineResponseDto>> response = restTemplate.exchange(
        "http://localhost:" + port + "/machines/identifier?condominiumId=" + condominiumId
            + "&identifier=" + identifier, GET, requestEntityWithDefaultHeaders,
        new ParameterizedTypeReference<StandardResponse<MachineResponseDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  void givenCondominiumDoesNotExists_whenGetMachineByIdentifier_thenNotFound() {
    String identifier = "test-identifier";
    String condominiumId = "99999";

    HttpEntity<Object> requestEntityWithDefaultHeaders = createRequestEntityWithDefaultHeaders(null);

    ResponseEntity<StandardResponse<MachineResponseDto>> response = restTemplate.exchange(
        "http://localhost:" + port + "/machines/identifier?condominiumId=" + condominiumId
            + "&identifier=" + identifier, GET, requestEntityWithDefaultHeaders,
        new ParameterizedTypeReference<StandardResponse<MachineResponseDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  void givenMachineId_whenGetMachineById_thenReturnMachine() {
    Long machineId = 101L;

    HttpEntity<Object> requestEntityWithDefaultHeaders = createRequestEntityWithDefaultHeaders(null);

    ResponseEntity<StandardResponse<MachineResponseDto>> response = restTemplate.exchange(
        "http://localhost:" + port + "/machines/" + machineId, GET, requestEntityWithDefaultHeaders,
        new ParameterizedTypeReference<StandardResponse<MachineResponseDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getData().id()).isEqualTo(machineId);
  }

  @Test
  void givenMachine_whenUpdateMachine_thenReturnUpdatedMachine() {
    Long machineId = 101L;
    Long condominiumId = 55L;
    MachineRequestDto machineRequestDto = MachineRequestDto.builder()
        .id(machineId)
        .identifier("updated-identifier")
        .condominiumId(condominiumId)
        .type("updated-type")
        .build();

    HttpEntity<MachineRequestDto> requestEntity = createRequestEntityWithDefaultHeaders(machineRequestDto);

    ResponseEntity<StandardResponse<MachineResponseDto>> response = restTemplate.exchange(
        "http://localhost:" + port + "/machines", PUT, requestEntity,
        new ParameterizedTypeReference<StandardResponse<MachineResponseDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getData().id()).isEqualTo(machineId);
    assertThat(response.getBody().getData().identifier()).isEqualTo(machineRequestDto.identifier());
    assertThat(response.getBody().getData().type()).isEqualTo(machineRequestDto.type());
  }

  @Test
  void givenMachineId_whenDeleteMachine_thenReturnNoContent() {
    String machineId = "101";

    HttpEntity<Object> requestEntityWithDefaultHeaders = createRequestEntityWithDefaultHeaders(null);

    ResponseEntity<StandardResponse<String>> response = restTemplate.exchange(
        "http://localhost:" + port + "/machines/" + machineId, DELETE, requestEntityWithDefaultHeaders,
        new ParameterizedTypeReference<StandardResponse<String>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void givenNothing_whenGetAllMachines_thenReturnAllMachinesWithStandardPagination() throws JsonProcessingException {
    HttpEntity<Object> requestEntityWithDefaultHeaders = createRequestEntityWithDefaultHeaders(null);

    ResponseEntity<StandardResponse<PaginationResponseDto<MachineResponseDto>>> response = restTemplate.exchange(
        "http://localhost:" + port + "/machines", GET, requestEntityWithDefaultHeaders,
        new ParameterizedTypeReference<StandardResponse<PaginationResponseDto<MachineResponseDto>>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().isSuccess()).isTrue();
    assertThat(response.getBody().getMessage()).isEqualTo("Machine found");
    assertThat(response.getBody().getTimestamp()).isNotNull();
    assertThat(response.getBody().getData()).isNotNull();
    assertThat(response.getBody().getData().content()).isNotNull();
    assertThat(response.getBody().getData().content()).hasSize(1);
    assertThat(response.getBody().getData().totalPages()).isEqualTo(1);
    assertThat(response.getBody().getData().totalElements()).isEqualTo(1);
    assertThat(response.getBody().getData().page()).isEqualTo(1);
    assertThat(response.getBody().getData().size()).isEqualTo(10);
    assertThat(response.getBody().getData().empty()).isFalse();
    assertThat(response.getBody().getData().first()).isTrue();
    assertThat(response.getBody().getData().last()).isTrue();

  }

  @ParameterizedTest
  @MethodSource("provideBrokenPaginationInfo")
  void givenSomePaginationPropertyIsInvalid_whenGetAllMachinesIsCalled_thenReturnBadRequest(Integer page,
                                                                                            Integer size,
                                                                                            String sortBy,
                                                                                            String direction,
                                                                                            String expectedMessage) {
    HttpEntity<Object> requestEntityWithDefaultHeaders = createRequestEntityWithDefaultHeaders(null);
    String url = "http://localhost:" + port + "/machines?page=" + page
        + "&size=" + size
        + "&sortBy=" + sortBy
        + "&direction=" + direction;
    ResponseEntity<StandardResponse<StandardErrorMessage>> response = restTemplate.exchange(
        url, GET, requestEntityWithDefaultHeaders,
        new ParameterizedTypeReference<StandardResponse<StandardErrorMessage>>() {
        });
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().isSuccess()).isFalse();
    assertThat(response.getBody().getMessage()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getData().details()).isEqualTo(expectedMessage);
    assertThat(response.getBody().getTimestamp()).isNotNull();

  }

  private static Stream<Arguments> provideBrokenPaginationInfo() {
    return Stream.of(
        Arguments.of(1, 10, "id", "INVALID", ObjectValidatorErrors.PAGINATION_DIRECTION_FORMAT_INVALID),
        Arguments.of(0, 10, "id", "ASC", ObjectValidatorErrors.PAGINATION_PAGE_INVALID),
        Arguments.of(1, 10, "INVALID", "ASC", "No property 'INVALID' found for type 'Machine'"),
        Arguments.of(1, -1, "id", "ASC", ObjectValidatorErrors.PAGINATION_SIZE_INVALID)
    );
  }

  private <T> HttpEntity<T> createRequestEntityWithDefaultHeaders(T body) {
    return new HttpEntity<>(body, new HttpHeaders() {
      {
        set("Content-Type", "application/json");
        set("Accept", "application/json");
        set("Authorization", "Bearer test_token");
      }
    });
  }

  private void mockTestJwtTokenWithManagerPermission() {
    Jwt mockjwt = Jwt.withTokenValue("test_token")
        .header("alg", "none")
        .claim("sub", "test_user")
        .claim("cognito:groups", new ArrayList<>(List.of("laundry-manager")))
        .build();
    when(jwtDecoder.decode("test_token")).thenReturn(mockjwt);
  }
}