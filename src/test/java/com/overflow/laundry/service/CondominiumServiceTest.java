package com.overflow.laundry.service;

import com.overflow.laundry.model.Condominium;
import com.overflow.laundry.model.dto.CondominiumDto;
import com.overflow.laundry.model.mapper.CondominiumMapper;
import com.overflow.laundry.repository.CondominiumRepository;
import com.overflow.laundry.service.impl.CondominiumServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CondominiumServiceTest {

  CondominiumService condominiumService;

  @Mock
  CondominiumRepository condominiumRepository;

  @InjectMocks
  CondominiumMapper condominiumMapper;

  @BeforeEach
  void setUp() {
    condominiumService = new CondominiumServiceImpl(condominiumRepository, condominiumMapper);
  }

  @Test
  void givenCondominium_whenCreateCondominiumIsCalled_thenCreateCondominium() {

    CondominiumDto condominiumDto = getTestCondominium();
    when(condominiumRepository.save(any(Condominium.class))).thenReturn(getMockCondominiumEntity());
    CondominiumDto condominiumCreated = condominiumService.createCondominium(condominiumDto);

    assertEquals(getMockCondominium(), condominiumCreated);

  }

  @Test
  void givenCondominiumWithId_whenCreateCondominiumIsCalled_thenReturnIllegalArgumentException() {

    CondominiumDto condominiumDto = CondominiumDto.builder()
        .id(1L)
        .name("Test Condominium")
        .address("123 Test St")
        .contactPhone("1234567890")
        .email("john@john.com")
        .build();

    assertThrows(IllegalArgumentException.class, () -> {
      condominiumService.createCondominium(condominiumDto);
    });

  }

  private static CondominiumDto getTestCondominium() {
    return CondominiumDto.builder()
        .name("Test Condominium")
        .address("123 Test St")
        .contactPhone("1234567890")
        .email("john@john.com")
        .build();
  }

  private static CondominiumDto getMockCondominium() {
    return CondominiumDto.builder()
        .id(1L)
        .name("Test Condominium")
        .address("123 Test St")
        .contactPhone("1234567890")
        .email("john@john.com")
        .build();
  }

  private static Condominium getMockCondominiumEntity() {
    return new Condominium(1L, "Test Condominium", "123 Test St", "1234567890", "john@john.com", null);
  }

}
