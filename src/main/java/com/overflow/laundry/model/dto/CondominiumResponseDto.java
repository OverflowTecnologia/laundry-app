package com.overflow.laundry.model.dto;

import lombok.Builder;

@Builder
public record CondominiumResponseDto(Long id,
                                     String name,
                                     String address,
                                     String contactPhone,
                                     String email) {
}
