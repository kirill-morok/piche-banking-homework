package com.pichebanking.api.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferFundsRequest(@NotNull Long sourceAccountId,
                                   @NotNull Long targetAccountId,
                                   @NotNull @Positive BigDecimal funds) {

    @AssertTrue
    public boolean isSourceAccountIsNotEqualTarget() {
        return !sourceAccountId.equals(targetAccountId);
    }
}
