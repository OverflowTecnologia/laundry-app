package com.overflow.laundry.model.dto;

import lombok.Builder;

@Builder
public record MachineResponseDto(Long id,
                                 String identifier,
                                 CondominiumResponseDto condominium,
                                 String type) {

}
