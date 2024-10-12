package com.pichebanking.api.dto.response;

import java.math.BigDecimal;

public record AccountsResponse(String fullName,
                               long accountNumber,
                               BigDecimal balance) {
}
