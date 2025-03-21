package com.overflow.laundry;

import com.overflow.laundry.config.StandardResponse;
import com.overflow.laundry.model.dto.MachineDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
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

  @Test
  public void givenMachine_whenCreateMachine_thenReturnMachineCreated() {

    MachineDto machineDto = MachineDto.builder()
        .identifier("identifier")
        .condominium("test-condominium")
        .type("test-type")
        .build();

    ResponseEntity<StandardResponse<MachineDto>> response = restTemplate.exchange(
        "http://localhost:" + port + "/machines", POST, new HttpEntity<>(machineDto),
        new ParameterizedTypeReference<StandardResponse<MachineDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody().getData().id()).isNotNull();
    assertThat(response.getBody().getData().identifier()).isEqualTo(machineDto.identifier());
    assertThat(response.getBody().getData().type()).isEqualTo(machineDto.type());
    assertThat(response.getBody().getData().condominium()).isEqualTo(machineDto.condominium());

  }

  @Test
  public void givenIdentifier_whenGetMachineByIdentifier_thenReturnMachine() {
    String identifier = "test-identifier";

    ResponseEntity<StandardResponse<MachineDto>> response = restTemplate.exchange(
        "http://localhost:" + port + "/machines/identifier/" + identifier,
        GET, null, new ParameterizedTypeReference<StandardResponse<MachineDto>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getData().identifier()).isEqualTo(identifier);
  }



}