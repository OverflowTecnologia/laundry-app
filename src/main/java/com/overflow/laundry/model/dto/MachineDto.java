package com.overflow.laundry.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import static com.overflow.laundry.constant.ObjectValidatorErrors.MACHINE_CONDOMINIUM_NOT_EMPTY_NULL;
import static com.overflow.laundry.constant.ObjectValidatorErrors.MACHINE_IDENTIFIER_NOT_EMPTY_NULL;
import static com.overflow.laundry.constant.ObjectValidatorErrors.MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL;

@Builder
public record MachineDto(Long id,
                          @NotEmpty(message = MACHINE_IDENTIFIER_NOT_EMPTY_NULL)
                          @NotNull(message = MACHINE_IDENTIFIER_NOT_EMPTY_NULL)
                          String identifier,
                          @NotEmpty(message = MACHINE_CONDOMINIUM_NOT_EMPTY_NULL)
                          @NotNull(message = MACHINE_CONDOMINIUM_NOT_EMPTY_NULL)
                          String condominium,
                          @NotEmpty(message = MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL)
                          @NotNull(message = MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL)
                          String type) {

}
