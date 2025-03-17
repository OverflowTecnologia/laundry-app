package com.overflow.laundry.exception;

import lombok.Builder;

@Builder
public record StandardErrorMessage(String details,
                                   String path) {
}
