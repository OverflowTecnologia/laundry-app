package com.overflow.laundry.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import static com.overflow.laundry.constant.ObjectValidatorErrors.MACHINE_CONDOMINIUM_ID_NOT_EMPTY_NULL;
import static com.overflow.laundry.constant.ObjectValidatorErrors.MACHINE_IDENTIFIER_NOT_EMPTY_NULL;
import static com.overflow.laundry.constant.ObjectValidatorErrors.MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL;

@Builder
public record MachineRequestDto(Long id,
                                @NotBlank(message = MACHINE_IDENTIFIER_NOT_EMPTY_NULL)
                                String identifier,
                                @NotNull(message = MACHINE_CONDOMINIUM_ID_NOT_EMPTY_NULL)
                                Long condominiumId,
                                @NotBlank(message = MACHINE_TYPE_OF_MACHINE_NOT_EMPTY_NULL)
                                String type) {
}
