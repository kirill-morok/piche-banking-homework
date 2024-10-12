package com.pichebanking.api.controller;

import com.pichebanking.api.dto.request.CreateAccountRequest;
import com.pichebanking.api.dto.response.AccountsResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/v1/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public interface AccountControllerApi {

    @PostMapping
    void createAccount(@RequestBody @Valid CreateAccountRequest request);

    @GetMapping("/{accountNumber}")
    AccountsResponse getAccount(@PathVariable long accountNumber);

    @GetMapping
    List<AccountsResponse> getAccounts();
}
