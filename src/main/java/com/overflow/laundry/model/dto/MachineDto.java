package com.overflow.laundry.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import static com.overflow.laundry.constant.ObjectValidatorErrors.MACHINE_CONDOMINIUM_NOT_EMPTY_NULL;
import static com.overflow.laundry.constant.ObjectValidatorErrors.MACHINE_IDENTIFIER_NOT_EMPTY_NULL;
import static com.overflow.laundry.constant.ObjectValidatorErrors.MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL;

@Builder
public record MachineDto(Long id,
                          @NotBlank(message = MACHINE_IDENTIFIER_NOT_EMPTY_NULL)
                          String identifier,
                          @NotBlank(message = MACHINE_CONDOMINIUM_NOT_EMPTY_NULL)
                          String condominium,
                          @NotBlank(message = MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL)
                          String type) {

}
