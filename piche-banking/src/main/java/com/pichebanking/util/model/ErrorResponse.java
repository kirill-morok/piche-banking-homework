package com.pichebanking.util.model;

import java.time.LocalDateTime;

public record ErrorResponse(String errorMessage, LocalDateTime timestamp) {

}