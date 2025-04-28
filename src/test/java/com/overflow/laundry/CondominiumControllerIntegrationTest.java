package com.overflow.laundry;

import com.overflow.laundry.config.StandardResponse;
import com.overflow.laundry.constant.ObjectValidatorErrors;
import com.overflow.laundry.exception.StandardErrorMessage;
import com.overflow.laundry.model.dto.CondominiumRequestDto;
import com.overflow.laundry.model.dto.CondominiumResponseDto;
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
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/init-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CondominiumControllerIntegrationTest {

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
  public void givenCondominium_whenCreateCondominium_thenReturnCondominiumCreated() {
    CondominiumRequestDto condominiumRequestDto = CondominiumRequestDto.builder()
        .name("Test Condominium")
        .address("123 Test St")
        .contactPhone("1234567890")
        .email("john@john.com")
        .build();
    HttpEntity<CondominiumRequestDto> requestEntity = createRequestEntityWithDefaultHeaders(condominiumRequestDto);

    ResponseEntity<StandardResponse<CondominiumResponseDto>> response = restTemplate.exchange(
        "http://localhost:" + port + "/condominiums", POST, requestEntity,
        new ParameterizedTypeReference<StandardResponse<CondominiumResponseDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getData().id()).isNotNull();
    assertThat(response.getBody().getData().name()).isEqualTo(condominiumRequestDto.name());
    assertThat(response.getBody().getData().address()).isEqualTo(condominiumRequestDto.address());
    assertThat(response.getBody().getData().contactPhone()).isEqualTo(condominiumRequestDto.contactPhone());
    assertThat(response.getBody().getData().email()).isEqualTo(condominiumRequestDto.email());

  }

  @Test
  void givenCondominiumId_whenGetCondominiumById_thenReturnCondominium() {
    Long condominiumId = 55L;

    HttpEntity<CondominiumRequestDto> requestEntity = createRequestEntityWithDefaultHeaders(null);

    ResponseEntity<StandardResponse<CondominiumResponseDto>> response = restTemplate.exchange(
        "http://localhost:" + port + "/condominiums/" + condominiumId, GET, requestEntity,
        new ParameterizedTypeReference<StandardResponse<CondominiumResponseDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getData().id()).isEqualTo(condominiumId);
    assertThat(response.getBody().getData().name()).isEqualTo("Central Park");
    assertThat(response.getBody().getData().address()).isEqualTo("123 Main St");
    assertThat(response.getBody().getData().contactPhone()).isEqualTo("123456789");
    assertThat(response.getBody().getData().email()).isEqualTo("test@test.com");

  }

  @Test
  void givenStandardPaginationInfo_whenGetAllCondominiums_thenReturnCondominiums() {
    int page = 1;
    int size = 10;
    String sortBy = "id";
    String direction = "ASC";

    HttpEntity<CondominiumRequestDto> requestEntity = createRequestEntityWithDefaultHeaders(null);

    ResponseEntity<StandardResponse<PaginationResponseDto<CondominiumResponseDto>>> response = restTemplate.exchange(
        "http://localhost:" + port + "/condominiums?page=" + page
            + "&size=" + size
            + "&sortBy=" + sortBy
            + "&direction=" + direction, GET, requestEntity,
        new ParameterizedTypeReference<StandardResponse<PaginationResponseDto<CondominiumResponseDto>>>() {
        });

    System.out.println("Response:" + response.getBody());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getData().content()).isNotEmpty();
    assertThat(response.getBody().getData().totalPages()).isEqualTo(1);
    assertThat(response.getBody().getData().totalElements()).isEqualTo(1);
  }

  @ParameterizedTest
  @MethodSource("provideBrokenPaginationInfo")
  void givenBrokenPaginationInfo_whenGetAllCondominiums_thenReturnBadRequest(
      int page, int size, String sortBy, String direction, String expectedErrorMessage) {

    HttpEntity<CondominiumRequestDto> requestEntity = createRequestEntityWithDefaultHeaders(null);

    String url = "http://localhost:" + port + "/condominiums?page=" + page
        + "&size=" + size
        + "&sortBy=" + sortBy
        + "&direction=" + direction;

    ResponseEntity<StandardResponse<StandardErrorMessage>> response = restTemplate.exchange(
        url, GET, requestEntity,
        new ParameterizedTypeReference<StandardResponse<StandardErrorMessage>>() {
        });

    System.out.println("Response:" + response.getBody());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getData()).isNotNull();
    assertThat(response.getBody().getData().details()).isEqualTo(expectedErrorMessage);

  }

  public static Stream<Arguments> provideBrokenPaginationInfo() {
    return Stream.of(
        Arguments.of(1, 10, "id", "INVALID", ObjectValidatorErrors.PAGINATION_DIRECTION_FORMAT_INVALID),
        Arguments.of(0, 10, "id", "ASC", ObjectValidatorErrors.PAGINATION_PAGE_INVALID),
        Arguments.of(1, 10, "INVALID", "ASC", "No property 'INVALID' found for type 'Condominium'"),
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