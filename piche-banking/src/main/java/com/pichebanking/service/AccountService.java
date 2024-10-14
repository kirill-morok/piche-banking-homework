package com.pichebanking.service;

import com.pichebanking.api.dto.request.CreateAccountRequest;
import com.pichebanking.dao.entity.Account;
import com.pichebanking.dao.repository.AccountRepository;
import com.pichebanking.exception.AccountNotFoundException;
import com.pichebanking.exception.InsufficientFundsException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

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

    public Account depositFunds(Long id, BigDecimal funds) {
        var account = findAccountWithLock(id);
        account.setBalance(account.getBalance().add(funds));
        return repository.save(account);
    }

    public Account withdrawFunds(Long id, BigDecimal funds) {
        var account = findAccountWithLock(id);
        if (account.getBalance().compareTo(funds) < 0) {
            throw new InsufficientFundsException(INSUFFICIENT_FUNDS_MSG);
        }
        account.setBalance(account.getBalance().subtract(funds));
        return repository.save(account);
    }

    public void transferFundsBetweenTwoAccounts(@NonNull Account source, @NonNull Account target, BigDecimal funds) {
        if (source.getBalance().compareTo(funds) < 0) {
            throw new InsufficientFundsException(INSUFFICIENT_FUNDS_MSG);
        }
        source.setBalance(source.getBalance().subtract(funds));
        target.setBalance(target.getBalance().add(funds));
        var accounts = List.of(source, target);
        repository.saveAll(accounts);
    }

    public List<Account> findAccountsWithLock(List<Long> ids) {
        return repository.findByIdInWithLock(ids);
    }

    private Account findAccountWithLock(long id) {
        return repository.findByIdWithLock(id)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MSG));
    }
}
