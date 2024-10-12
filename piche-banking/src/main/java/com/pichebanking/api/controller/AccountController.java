package com.pichebanking.api.controller;

import com.pichebanking.api.dto.request.CreateAccountRequest;
import com.pichebanking.api.dto.response.AccountsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class AccountController implements AccountControllerApi {

    @Override
    public void createAccount(CreateAccountRequest request) {

    }

    @Override
    public AccountsResponse getAccount(long accountNumber) {
        return null;
    }

    @Override
    public List<AccountsResponse> getAccounts() {
        return null;
    }
}
