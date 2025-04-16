package com.overflow.laundry.model.dto;

import lombok.Builder;

@Builder
public record MachineResponseDto(Long id,
                                 String identifier,
                                 CondominiumDto condominium,
                                 String type) {

}
