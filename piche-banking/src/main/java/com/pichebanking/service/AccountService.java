package com.pichebanking.service;

import com.pichebanking.api.dto.request.CreateAccountRequest;
import com.pichebanking.api.dto.request.TransferFundsRequest;
import com.pichebanking.dao.entity.Account;
import com.pichebanking.dao.repository.AccountRepository;
import com.pichebanking.exception.AccountNotFoundException;
import com.pichebanking.exception.InsufficientFundsException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.pichebanking.util.constant.ExceptionMessage.ACCOUNT_NOT_FOUND_MSG;
import static com.pichebanking.util.constant.ExceptionMessage.INSUFFICIENT_FUNDS_MSG;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final ConversionService conversionService;

    public Account createAccount(CreateAccountRequest createAccountRequest) {
        var account = Objects.requireNonNull(conversionService.convert(createAccountRequest, Account.class));
        return repository.save(account);
    }

    public Account getAccount(long accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MSG));
    }

    public List<Account> getAccounts() {
        return repository.findAll();
    }

    @Transactional
    public void depositFunds(Long id, BigDecimal funds) {
        var account = findAccountWithLock(id);
        account.setBalance(account.getBalance().add(funds));
        repository.save(account);
    }

    @Transactional
    public void withdrawFunds(Long id, BigDecimal funds) {
        var account = findAccountWithLock(id);
        if (account.getBalance().compareTo(funds) < 0) {
            throw new InsufficientFundsException(INSUFFICIENT_FUNDS_MSG);
        }
        account.setBalance(account.getBalance().subtract(funds));
        repository.save(account);
    }

    @Transactional
    public void transferFunds(TransferFundsRequest transferFundsRequest) {
        var accountIds = List.of(transferFundsRequest.sourceAccountId(), transferFundsRequest.targetAccountId());
        var accounts = findAccountsWithLock(accountIds);
        var sourceAccount = getAccountFromList(accounts, transferFundsRequest.sourceAccountId());
        var transferAccount = getAccountFromList(accounts, transferFundsRequest.targetAccountId());
        if (sourceAccount.getBalance().compareTo(transferFundsRequest.funds()) < 0) {
            throw new InsufficientFundsException(INSUFFICIENT_FUNDS_MSG);
        }
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(transferFundsRequest.funds()));
        transferAccount.setBalance(transferAccount.getBalance().add(transferFundsRequest.funds()));
        repository.saveAll(accounts);
    }

    private Account findAccountWithLock(long id) {
        return repository.findByIdWithLock(id)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MSG));
    }

    private List<Account> findAccountsWithLock(List<Long> ids) {
        return repository.findByIdInWithLock(ids);
    }

    private Account getAccountFromList(List<Account> accounts, long accountId) {
        return accounts.stream().filter(account -> account.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MSG));
    }
}
