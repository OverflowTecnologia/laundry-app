package com.overflow.laundry.model.dto;

import com.overflow.laundry.constant.ObjectValidatorErrors;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MachineDto(Long id,
                          @NotEmpty(message = ObjectValidatorErrors.MACHINE_IDENTIFIER_NOT_EMPTY_NULL)
                          @NotNull(message = ObjectValidatorErrors.MACHINE_IDENTIFIER_NOT_EMPTY_NULL)
                          String identifier,
                          @NotEmpty(message = ObjectValidatorErrors.MACHINE_CONDOMINIUM_NOT_EMPTY_NULL)
                          @NotNull(message = ObjectValidatorErrors.MACHINE_CONDOMINIUM_NOT_EMPTY_NULL)
                          String condominium,
                          @NotEmpty(message = ObjectValidatorErrors.MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL)
                          @NotNull(message = ObjectValidatorErrors.MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL)
                          String type) {

}
