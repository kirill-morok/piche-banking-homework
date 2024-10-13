package com.pichebanking.api.dto.response;

import java.math.BigDecimal;

public record AccountResponse(String fullName,
                              long accountNumber,
                              BigDecimal balance) {
}
