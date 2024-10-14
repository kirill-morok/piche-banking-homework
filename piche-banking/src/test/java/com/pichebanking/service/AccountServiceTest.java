package com.pichebanking.service;

import com.pichebanking.api.dto.request.CreateAccountRequest;
import com.pichebanking.dao.entity.Account;
import com.pichebanking.dao.repository.AccountRepository;
import com.pichebanking.exception.AccountNotFoundException;
import com.pichebanking.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class AccountServiceTest {

    private static final String USER_FULL_NAME = "Test Name";

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountTest() {
        var request = new CreateAccountRequest(USER_FULL_NAME, BigDecimal.TEN);
        var expected = new Account().setBalance(BigDecimal.TEN).setFullName(USER_FULL_NAME);

        Mockito.when(conversionService.convert(request, Account.class)).thenReturn(expected);
        Mockito.when(accountRepository.save(expected)).thenReturn(expected.setId(1L));

        var actual = accountService.createAccount(request);

        assertEquals(actual, expected);
    }

    @Test
    void getAccountTest() {
        var id = 1L;
        var expected = new Account().setBalance(BigDecimal.TEN).setId(1L).setFullName(USER_FULL_NAME);

        Mockito.when(accountRepository.findById(id)).thenReturn(Optional.ofNullable(expected));

        var actual = accountService.getAccount(id);

        assertEquals(actual, expected);
    }

    @Test
    void getAccountWithNotFoundExceptionTest() {
        var id = 1L;

        Mockito.when(accountRepository.findById(id)).thenThrow(AccountNotFoundException.class);

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccount(id));
    }

    @Test
    void getAccountsTest() {
        var expected = List.of(new Account().setBalance(BigDecimal.TEN).setId(1L).setFullName(USER_FULL_NAME));

        Mockito.when(accountRepository.findAll()).thenReturn(expected);

        var actual = accountService.getAccounts();

        assertEquals(actual, expected);
    }

    @Test
    void depositFundsTest() {
        var id = 1L;
        var funds = BigDecimal.valueOf(20);
        var expected = new Account().setBalance(BigDecimal.TEN).setId(1L).setFullName(USER_FULL_NAME);

        Mockito.when(accountRepository.findByIdWithLock(id)).thenReturn(Optional.ofNullable(expected));
        Mockito.when(accountRepository.save(expected.setBalance(funds))).thenReturn(expected);

        var actual = accountService.depositFunds(id, BigDecimal.TEN);

        Mockito.verify(accountRepository, Mockito.times(1)).findByIdWithLock(id);
        Mockito.verify(accountRepository, Mockito.times(1)).save(expected);

        assertEquals(actual, expected);
    }

    @Test
    void depositFundsWithNotFoundExceptionTest() {
        var id = 1L;

        Mockito.when(accountRepository.findByIdWithLock(id)).thenThrow(AccountNotFoundException.class);

        assertThrows(AccountNotFoundException.class, () -> accountService.depositFunds(id, BigDecimal.TEN));
    }

    @Test
    void withdrawFundsTest() {
        var id = 1L;
        var accountOne = new Account().setBalance(BigDecimal.TEN).setId(1L).setFullName(USER_FULL_NAME);
        var expected = new Account().setBalance(BigDecimal.ZERO).setId(1L).setFullName(USER_FULL_NAME);

        Mockito.when(accountRepository.findByIdWithLock(id)).thenReturn(Optional.ofNullable(accountOne));
        Mockito.when(accountRepository.save(expected)).thenReturn(expected);

        var actual = accountService.withdrawFunds(id, BigDecimal.TEN);

        Mockito.verify(accountRepository, Mockito.times(1)).findByIdWithLock(id);
        Mockito.verify(accountRepository, Mockito.times(1)).save(expected);

        assertEquals(actual, expected);
    }

    @Test
    void withdrawFundsWithNotFoundExceptionTest() {
        var id = 1L;

        Mockito.when(accountRepository.findByIdWithLock(id)).thenThrow(AccountNotFoundException.class);

        assertThrows(AccountNotFoundException.class, () -> accountService.withdrawFunds(id, BigDecimal.TEN));
    }

    @Test
    void withdrawFundsWithInsufficientExceptionTest() {
        var id = 1L;
        var account = new Account().setBalance(BigDecimal.ONE).setId(1L).setFullName(USER_FULL_NAME);

        Mockito.when(accountRepository.findByIdWithLock(id)).thenReturn(Optional.ofNullable(account));

        assertThrows(InsufficientFundsException.class, () -> accountService.withdrawFunds(id, BigDecimal.TEN));
    }

    @Test
    void transferFundsTest() {
        var accountSource = new Account().setBalance(BigDecimal.TEN).setId(1L).setFullName(USER_FULL_NAME);
        var accountTarget = new Account().setBalance(BigDecimal.ZERO).setId(2L).setFullName(USER_FULL_NAME);

        var accountSourceChanged = new Account().setBalance(BigDecimal.ZERO).setId(1L).setFullName(USER_FULL_NAME);
        var accountTargetChanged = new Account().setBalance(BigDecimal.TEN).setId(2L).setFullName(USER_FULL_NAME);

        var listAccountsChanged = List.of(accountSourceChanged, accountTargetChanged);

        Mockito.when(accountRepository.saveAll(listAccountsChanged)).thenReturn(listAccountsChanged);

        accountService.transferFundsBetweenTwoAccounts(accountSource, accountTarget, BigDecimal.TEN);

        Mockito.verify(accountRepository, Mockito.times(1)).saveAll(listAccountsChanged);
    }

    @Test
    void transferFundsInsufficientExceptionTest() {
        var accountSource = new Account().setBalance(BigDecimal.ONE).setId(1L).setFullName(USER_FULL_NAME);
        var accountTarget = new Account().setBalance(BigDecimal.ZERO).setId(2L).setFullName(USER_FULL_NAME);

        assertThrows(InsufficientFundsException.class,
                () -> accountService.transferFundsBetweenTwoAccounts(accountSource, accountTarget, BigDecimal.TEN));
    }

    @Test
    void findAccountsWithLockTest() {
        var accountOne = new Account().setBalance(BigDecimal.TEN).setId(1L).setFullName(USER_FULL_NAME);
        var accountTwo = new Account().setBalance(BigDecimal.ZERO).setId(2L).setFullName(USER_FULL_NAME);
        var expected = List.of(accountOne, accountTwo);
        var ids = List.of(1L, 2L);

        Mockito.when(accountRepository.findByIdInWithLock(ids)).thenReturn(expected);

        var actual = accountService.findAccountsWithLock(ids);

        assertEquals(actual, expected);
    }
}