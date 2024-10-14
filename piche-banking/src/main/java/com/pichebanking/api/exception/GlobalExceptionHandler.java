package com.pichebanking.api.exception;

import com.pichebanking.exception.AccountNotFoundException;
import com.pichebanking.exception.InsufficientFundsException;
import com.pichebanking.util.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(toErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(toErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    private ErrorResponse toErrorResponse(String errorMessage, LocalDateTime timestamp) {
        return new ErrorResponse(
                errorMessage,
                timestamp
        );
    }
}
