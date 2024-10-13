package com.pichebanking.model;

import java.time.LocalDateTime;

public record ErrorResponse(String errorMessage, LocalDateTime timestamp) {

}