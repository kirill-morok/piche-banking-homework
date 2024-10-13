package com.pichebanking.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateAccountRequest(@NotBlank String fullName,
                                   @NotNull @Positive BigDecimal initialBalance) {
}
