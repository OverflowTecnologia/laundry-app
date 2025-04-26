package com.overflow.laundry.service;

import com.overflow.laundry.exception.CondominiumNotFoundException;
import com.overflow.laundry.model.Condominium;
import com.overflow.laundry.model.dto.CondominiumRequestDto;
import com.overflow.laundry.model.dto.CondominiumResponseDto;
import com.overflow.laundry.model.mapper.CondominiumMapper;
import com.overflow.laundry.repository.CondominiumRepository;
import com.overflow.laundry.service.impl.CondominiumServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

    CondominiumRequestDto condominiumRequestDto = getTestCondominium();
    when(condominiumRepository.save(any(Condominium.class))).thenReturn(getMockCondominiumEntity());
    CondominiumResponseDto condominiumCreated = condominiumService.createCondominium(condominiumRequestDto);

    assertEquals(getMockCondominiumResponseDto(), condominiumCreated);

  }

  @Test
  void givenCondominiumWithId_whenCreateCondominiumIsCalled_thenReturnIllegalArgumentException() {

    CondominiumRequestDto condominiumRequestDto = CondominiumRequestDto.builder()
        .id(1L)
        .name("Test Condominium")
        .address("123 Test St")
        .contactPhone("1234567890")
        .email("john@john.com")
        .build();

    assertThrows(IllegalArgumentException.class, () -> {
      condominiumService.createCondominium(condominiumRequestDto);
    });

  }

  @Test
  void givenId_whenGetCondominiumById_thenReturnCondominium() {
    Long id = 1L;
    when(condominiumRepository.findById(id)).thenReturn(Optional.of(getMockCondominiumEntity()));
    CondominiumResponseDto condominiumResponseDto = condominiumService.getCondominiumById(id);

    assertEquals(getMockCondominiumResponseDto(), condominiumResponseDto);
  }

  @Test
  void givenId_whenGetCondominiumById_thenReturnCondominiumNotFoundException() {
    Long id = 1L;
    when(condominiumRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(CondominiumNotFoundException.class, () -> {
      condominiumService.getCondominiumById(id);
    });
  }

  private static CondominiumRequestDto getTestCondominium() {
    return CondominiumRequestDto.builder()
        .name("Test Condominium")
        .address("123 Test St")
        .contactPhone("1234567890")
        .email("john@john.com")
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

  private static Condominium getMockCondominiumEntity() {
    return new Condominium(1L, "Test Condominium", "123 Test St", "1234567890", "john@john.com", null);
  }

}
