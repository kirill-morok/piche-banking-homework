package com.pichebanking.api.controller;

import com.pichebanking.api.dto.request.FundsRequest;
import com.pichebanking.api.dto.request.TransferFundsRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/v1/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
public interface TransactionControllerApi {

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping
    void transferFunds(@RequestBody @Valid TransferFundsRequest request);

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping("/accounts/{id}/deposit")
    void depositFunds(@PathVariable Long id, @RequestBody @Valid FundsRequest request);

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping("/accounts/{id}/withdraw")
    void withdrawFunds(@PathVariable Long id, @RequestBody @Valid FundsRequest request);
}
