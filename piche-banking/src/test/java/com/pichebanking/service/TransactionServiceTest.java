package com.pichebanking.service;

import com.pichebanking.api.dto.request.TransferFundsRequest;
import com.pichebanking.dao.entity.Account;
import com.pichebanking.dao.entity.PicheTransaction;
import com.pichebanking.dao.repository.PicheTransactionRepository;
import com.pichebanking.exception.AccountNotFoundException;
import com.pichebanking.exception.InsufficientFundsException;
import com.pichebanking.util.constant.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class TransactionServiceTest {

    private static final String USER_FULL_NAME = "Test Name";

    @Mock
    private PicheTransactionRepository repository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void transferFundsTest() {
        var request = new TransferFundsRequest(1L, 2L, BigDecimal.TEN);
        var ids = List.of(request.sourceAccountId(), request.targetAccountId());
        var accountSource = new Account().setBalance(BigDecimal.ZERO).setId(1L).setFullName(USER_FULL_NAME);
        var accountTarget = new Account().setBalance(BigDecimal.TEN).setId(2L).setFullName(USER_FULL_NAME);
        var accounts = List.of(accountSource, accountTarget);
        var transaction = new PicheTransaction().setTransactionType(TransactionType.TRANSFER).setFunds(BigDecimal.TEN)
                .setSourceAccount(accountSource).setTargetAccount(accountTarget);

        Mockito.when(accountService.findAccountsWithLock(ids)).thenReturn(accounts);
        Mockito.when(repository.save(transaction)).thenReturn(transaction);

        transactionService.transferFunds(request);

        Mockito.verify(accountService, Mockito.times(1))
                .transferFundsBetweenTwoAccounts(accountSource, accountTarget, BigDecimal.TEN);
    }

    @Test
    void transferFundsWithInsufficientFundsExceptionTest() {
        var request = new TransferFundsRequest(1L, 2L, BigDecimal.TEN);
        var ids = List.of(request.sourceAccountId(), request.targetAccountId());

        Mockito.when(accountService.findAccountsWithLock(ids)).thenThrow(InsufficientFundsException.class);

        assertThrows(InsufficientFundsException.class, () -> transactionService.transferFunds(request));

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void transferFundsWithAccountNotFoundExceptionTest() {
        var request = new TransferFundsRequest(1L, 2L, BigDecimal.TEN);
        var ids = List.of(request.sourceAccountId(), request.targetAccountId());
        var accountSource = new Account().setBalance(BigDecimal.ZERO).setId(1L).setFullName(USER_FULL_NAME);
        var accounts = Collections.singletonList(accountSource);

        Mockito.when(accountService.findAccountsWithLock(ids)).thenReturn(accounts);

        assertThrows(AccountNotFoundException.class, () -> transactionService.transferFunds(request));

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void depositFundsTest() {
        var account = new Account().setBalance(BigDecimal.TEN).setId(1L).setFullName(USER_FULL_NAME);
        var transaction = new PicheTransaction().setTransactionType(TransactionType.DEPOSIT).setFunds(BigDecimal.TEN)
                .setSourceAccount(account).setTargetAccount(null);

        Mockito.when(accountService.depositFunds(1L, BigDecimal.TEN)).thenReturn(account);

        transactionService.depositFunds(1L, BigDecimal.TEN);

        Mockito.verify(repository, Mockito.times(1)).save(transaction);
    }

    @Test
    void depositFundsWithAccountNotFoundExceptionTest() {
        Mockito.when(accountService.depositFunds(1L, BigDecimal.TEN)).thenThrow(AccountNotFoundException.class);

        assertThrows(AccountNotFoundException.class, () -> transactionService.depositFunds(1L, BigDecimal.TEN));

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void withdrawFundsTest() {
        var account = new Account().setBalance(BigDecimal.ZERO).setId(1L).setFullName(USER_FULL_NAME);
        var transaction = new PicheTransaction().setTransactionType(TransactionType.WITHDRAW).setFunds(BigDecimal.TEN)
                .setSourceAccount(account).setTargetAccount(null);

        Mockito.when(accountService.withdrawFunds(1L, BigDecimal.TEN)).thenReturn(account);

        transactionService.withdrawFunds(1L, BigDecimal.TEN);

        Mockito.verify(repository, Mockito.times(1)).save(transaction);
    }

    @Test
    void withdrawFundsWithInsufficientFundsExceptionTest() {
        Mockito.when(accountService.withdrawFunds(1L, BigDecimal.TEN)).thenThrow(InsufficientFundsException.class);

        assertThrows(InsufficientFundsException.class, () -> transactionService.withdrawFunds(1L, BigDecimal.TEN));

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void withdrawFundsWithAccountNotFoundExceptionTest() {
        Mockito.when(accountService.withdrawFunds(1L, BigDecimal.TEN)).thenThrow(AccountNotFoundException.class);

        assertThrows(AccountNotFoundException.class, () -> transactionService.withdrawFunds(1L, BigDecimal.TEN));

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }

}