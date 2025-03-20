package com.overflow.laundry.exception;

import java.util.List;

public record ErrorResponse(String message, List<String> details) {
}
