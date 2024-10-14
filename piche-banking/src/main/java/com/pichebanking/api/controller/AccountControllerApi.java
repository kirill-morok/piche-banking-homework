package com.pichebanking.api.controller;

import com.pichebanking.api.dto.request.CreateAccountRequest;
import com.pichebanking.api.dto.response.AccountResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/v1/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public interface AccountControllerApi {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    AccountResponse createAccount(@RequestBody @Valid CreateAccountRequest request);

    @GetMapping("/{id}")
    AccountResponse getAccount(@PathVariable long id);

    @GetMapping
    List<AccountResponse> getAccounts();
}
