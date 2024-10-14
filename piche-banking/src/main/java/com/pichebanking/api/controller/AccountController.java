package com.pichebanking.api.controller;

import com.pichebanking.api.dto.request.CreateAccountRequest;
import com.pichebanking.api.dto.request.FundsRequest;
import com.pichebanking.api.dto.request.TransferFundsRequest;
import com.pichebanking.api.dto.response.AccountResponse;
import com.pichebanking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class AccountController implements AccountControllerApi {

    private final AccountService accountService;
    private final ConversionService conversionService;

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {
        return conversionService.convert(accountService.createAccount(request), AccountResponse.class);
    }

    @Override
    public AccountResponse getAccount(long id) {
        return conversionService.convert(accountService.getAccount(id), AccountResponse.class);
    }

    @Override
    public List<AccountResponse> getAccounts() {
        return accountService.getAccounts().stream()
                .map(account -> conversionService.convert(account, AccountResponse.class))
                .toList();
    }
}
