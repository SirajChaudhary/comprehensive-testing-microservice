package com.example.comprehensivetesting.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotBlank(message = "Holder name is required")
        String holderName,

        @NotNull(message = "Initial balance is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be non negative")
        BigDecimal initialBalance
) {
}