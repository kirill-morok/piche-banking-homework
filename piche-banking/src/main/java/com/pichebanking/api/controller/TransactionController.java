package com.pichebanking.api.controller;

import com.pichebanking.api.dto.request.FundsRequest;
import com.pichebanking.api.dto.request.TransferFundsRequest;
import com.pichebanking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class TransactionController implements TransactionControllerApi {

    private final TransactionService transactionService;

    @Override
    public void transferFunds(TransferFundsRequest request) {
        transactionService.transferFunds(request);
    }

    @Override
    public void depositFunds(Long id, FundsRequest request) {
        transactionService.depositFunds(id, request.funds());
    }

    @Override
    public void withdrawFunds(Long id, FundsRequest request) {
        transactionService.withdrawFunds(id, request.funds());
    }
}
