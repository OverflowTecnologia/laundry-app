package com.overflow.laundry;

import com.overflow.laundry.config.StandardResponse;
import com.overflow.laundry.model.dto.MachineRequestDto;
import com.overflow.laundry.model.dto.MachineResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

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
        "http://localhost:" + port + "/machines/identifier/test-identifier",
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
        "http://localhost:" + port + "/machines/identifier/test-identifier",
        GET, requestEntityWithDefaultHeaders, new ParameterizedTypeReference<StandardResponse<MachineResponseDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  public void givenMachine_whenCreateMachine_thenReturnMachineCreated() {

    MachineRequestDto machineRequestDto = MachineRequestDto.builder()
        .identifier("identifier")
        .condominiumId(1L)
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
  void givenIdentifier_whenGetMachineByIdentifier_thenReturnMachine() {
    String identifier = "test-identifier";

    HttpEntity<Object> requestEntityWithDefaultHeaders = createRequestEntityWithDefaultHeaders(null);

    ResponseEntity<StandardResponse<MachineResponseDto>> response = restTemplate.exchange(
        "http://localhost:" + port + "/machines/identifier/" + identifier,
        GET, requestEntityWithDefaultHeaders, new ParameterizedTypeReference<StandardResponse<MachineResponseDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getData().identifier()).isEqualTo(identifier);
    assertThat(response.getBody().getData().id()).isNotNull();
    assertThat(response.getBody().getData().type()).isEqualTo("Washer");
    assertThat(response.getBody().getData().condominium().id()).isEqualTo(1L);

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