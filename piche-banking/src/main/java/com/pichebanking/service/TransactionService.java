package com.pichebanking.service;

import com.pichebanking.api.dto.request.TransferFundsRequest;
import com.pichebanking.dao.entity.Account;
import com.pichebanking.dao.entity.PicheTransaction;
import com.pichebanking.dao.repository.PicheTransactionRepository;
import com.pichebanking.exception.AccountNotFoundException;
import com.pichebanking.util.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.pichebanking.util.constant.ExceptionMessage.ACCOUNT_NOT_FOUND_MSG;
import static com.pichebanking.util.enums.TransactionType.*;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final PicheTransactionRepository repository;
    private final AccountService accountService;

    @Transactional
    public void transferFunds(TransferFundsRequest request) {
        var accountsIds = List.of(request.sourceAccountId(), request.targetAccountId());
        var accounts = accountService.findAccountsWithLock(accountsIds);
        var sourceAccount = getAccountFromList(accounts, request.sourceAccountId());
        var targetAccount = getAccountFromList(accounts, request.targetAccountId());
        accountService.transferFundsBetweenTwoAccounts(sourceAccount, targetAccount, request.funds());
        var transaction = constructTransaction(sourceAccount, targetAccount, request.funds(), TRANSFER);
        repository.save(transaction);
    }

    @Transactional
    public void depositFunds(Long id, BigDecimal funds) {
        var account = accountService.depositFunds(id, funds);
        var transaction = constructTransaction(account, null, funds, DEPOSIT);
        repository.save(transaction);
    }

    @Transactional
    public void withdrawFunds(Long id, BigDecimal funds) {
        var account = accountService.withdrawFunds(id, funds);
        var transaction = constructTransaction(account, null, funds, WITHDRAW);
        repository.save(transaction);
    }

    private Account getAccountFromList(List<Account> accounts, long accountId) {
        return accounts.stream().filter(account -> account.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MSG));
    }

    private PicheTransaction constructTransaction(Account sourceAccount, Account targetAccount,
                                                  BigDecimal funds, TransactionType type) {

        return new PicheTransaction()
                .setTransactionType(type)
                .setFunds(funds)
                .setSourceAccount(sourceAccount)
                .setTargetAccount(targetAccount);
    }
}
