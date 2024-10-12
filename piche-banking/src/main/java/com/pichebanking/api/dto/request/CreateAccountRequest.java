package com.pichebanking.api.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateAccountRequest(@NotBlank String fullName,
                                   @NotBlank BigDecimal initialBalance) {
}
