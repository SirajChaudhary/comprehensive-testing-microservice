package com.example.comprehensivetesting.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        String message,
        int status,
        String path
) {
}