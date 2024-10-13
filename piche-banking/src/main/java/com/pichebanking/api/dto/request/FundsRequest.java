package com.pichebanking.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record FundsRequest(@NotNull @Positive BigDecimal funds) {
}
