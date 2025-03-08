package com.overflow.laundry.model.dto;

import com.overflow.laundry.constant.ValidatorErrors;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MachineDto (Long id,
                          @NotEmpty(message = ValidatorErrors.MACHINE_IDENTIFIER_NOT_EMPTY_NULL)
                          @NotNull(message = ValidatorErrors.MACHINE_IDENTIFIER_NOT_EMPTY_NULL)
                          String identifier,
                          @NotEmpty(message = ValidatorErrors.MACHINE_CONDOMINIUM_NOT_EMPTY_NULL)
                          @NotNull(message = ValidatorErrors.MACHINE_CONDOMINIUM_NOT_EMPTY_NULL)
                          String condominium,
                          @NotEmpty(message = ValidatorErrors.MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL)
                          @NotNull(message = ValidatorErrors.MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL)
                          String typeOfMachine) {

}
