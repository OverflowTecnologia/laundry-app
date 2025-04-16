package com.overflow.laundry.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import static com.overflow.laundry.constant.ObjectValidatorErrors.CONDOMINIUM_ADDRESS_NOT_EMPTY_NULL;
import static com.overflow.laundry.constant.ObjectValidatorErrors.CONDOMINIUM_CONTACT_NOT_EMPTY_NULL;
import static com.overflow.laundry.constant.ObjectValidatorErrors.CONDOMINIUM_EMAIL_FORMAT_NOT_VALID;
import static com.overflow.laundry.constant.ObjectValidatorErrors.CONDOMINIUM_EMAIL_NOT_EMPTY_NULL;
import static com.overflow.laundry.constant.ObjectValidatorErrors.CONDOMINIUM_NAME_NOT_EMPTY_NULL;

@Builder
public record CondominiumDto(Long id,
                             @NotBlank(message = CONDOMINIUM_NAME_NOT_EMPTY_NULL)
                             String name,
                             @NotBlank(message = CONDOMINIUM_ADDRESS_NOT_EMPTY_NULL)
                             String address,
                             @NotBlank(message = CONDOMINIUM_CONTACT_NOT_EMPTY_NULL)
                             String contactPhone,
                             @NotBlank(message = CONDOMINIUM_EMAIL_NOT_EMPTY_NULL)
                             @Email(message = CONDOMINIUM_EMAIL_FORMAT_NOT_VALID)
                             String email) {
}
