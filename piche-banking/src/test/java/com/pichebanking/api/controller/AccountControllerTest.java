package com.pichebanking.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pichebanking.api.dto.request.CreateAccountRequest;
import com.pichebanking.api.dto.request.FundsRequest;
import com.pichebanking.api.dto.request.TransferFundsRequest;
import com.pichebanking.api.dto.response.AccountResponse;
import com.pichebanking.api.exception.GlobalExceptionHandler;
import com.pichebanking.dao.entity.Account;
import com.pichebanking.exception.AccountNotFoundException;
import com.pichebanking.exception.InsufficientFundsException;
import com.pichebanking.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static com.pichebanking.util.constant.ExceptionMessage.ACCOUNT_NOT_FOUND_MSG;
import static com.pichebanking.util.constant.ExceptionMessage.INSUFFICIENT_FUNDS_MSG;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class AccountControllerTest {

    private static final String ACCOUNT_PATH = "/v1/accounts";
    private static final String ACCOUNT_PATH_WITH_ID = "/v1/accounts/{id}";
    private static final String ACCOUNT_PATH_WITH_ID_DEPOSIT = "/v1/accounts/{id}/deposit";
    private static final String ACCOUNT_PATH_WITH_ID_WITHDRAW = "/v1/accounts/{id}/withdraw";
    private static final String ACCOUNT_PATH_TRANSFER = "/v1/accounts/transfer";

    private static final String USER_FULL_NAME = "Test Name";

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AccountService accountService;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void createAccountTest() throws Exception {
        var request = new CreateAccountRequest(USER_FULL_NAME, BigDecimal.TEN);
        var account = new Account().setBalance(BigDecimal.TEN).setId(1L).setFullName(USER_FULL_NAME);
        var accountResponse = new AccountResponse(USER_FULL_NAME, 1L, BigDecimal.TEN);
        var expected = objectMapper.writeValueAsString(accountResponse);

        Mockito.when(accountService.createAccount(request)).thenReturn(account);
        Mockito.when(conversionService.convert(account, AccountResponse.class)).thenReturn(accountResponse);

        mockMvc.perform(post(ACCOUNT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(expected));
    }

    @Test
    void createAccountWithNullArgumentTest() throws Exception {
        var fullName = "Test Name";
        var request = new CreateAccountRequest(fullName, null);

        mockMvc.perform(post(ACCOUNT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAccountWithEmptyFieldTest() throws Exception {
        var request = new CreateAccountRequest(" ", BigDecimal.TEN);

        mockMvc.perform(post(ACCOUNT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAccountTest() throws Exception {
        var account = new Account().setBalance(BigDecimal.TEN).setId(1L).setFullName(USER_FULL_NAME);
        var accountResponse = new AccountResponse(USER_FULL_NAME, 1L, BigDecimal.TEN);
        var expected = objectMapper.writeValueAsString(accountResponse);

        Mockito.when(accountService.getAccount(1L)).thenReturn(account);
        Mockito.when(conversionService.convert(account, AccountResponse.class)).thenReturn(accountResponse);

        mockMvc.perform(get(ACCOUNT_PATH_WITH_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void getAccountWithNotFoundExceptionTest() throws Exception {
        Mockito.when(accountService.getAccount(5L))
                .thenThrow(new AccountNotFoundException(ACCOUNT_NOT_FOUND_MSG));

        mockMvc.perform(get(ACCOUNT_PATH_WITH_ID, 5L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(ACCOUNT_NOT_FOUND_MSG));
    }

    @Test
    void getAccountsTest() throws Exception {
        var account = new Account().setBalance(BigDecimal.TEN).setId(1L).setFullName(USER_FULL_NAME);
        var accounts = Collections.singletonList(account);
        var accountResponse = new AccountResponse(USER_FULL_NAME, 1L, BigDecimal.TEN);
        var expected = objectMapper.writeValueAsString(Collections.singletonList(accountResponse));

        Mockito.when(accountService.getAccounts()).thenReturn(accounts);
        Mockito.when(conversionService.convert(account, AccountResponse.class)).thenReturn(accountResponse);

        mockMvc.perform(get(ACCOUNT_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void depositFundsTest() throws Exception {
        var id = 1L;
        var request = new FundsRequest(BigDecimal.TEN);

        mockMvc.perform(patch(ACCOUNT_PATH_WITH_ID_DEPOSIT, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isAccepted());

        Mockito.verify(accountService, Mockito.times(1)).depositFunds(id, request.funds());
    }

    @Test
    void depositFundsWithZeroTest() throws Exception {
        var id = 1L;
        var request = new FundsRequest(BigDecimal.ZERO);

        mockMvc.perform(patch(ACCOUNT_PATH_WITH_ID_DEPOSIT, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());

        Mockito.verify(accountService, Mockito.times(0)).depositFunds(id, request.funds());
    }

    @Test
    void depositFundsWithExceptionTest() throws Exception {
        var id = 1L;
        var request = new FundsRequest(BigDecimal.TEN);

        doThrow(new AccountNotFoundException(ACCOUNT_NOT_FOUND_MSG))
                .when(accountService).depositFunds(id, request.funds());

        mockMvc.perform(patch(ACCOUNT_PATH_WITH_ID_DEPOSIT, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(ACCOUNT_NOT_FOUND_MSG));
    }

    @Test
    void withdrawFundsTest() throws Exception {
        var id = 1L;
        var request = new FundsRequest(BigDecimal.TEN);

        mockMvc.perform(patch(ACCOUNT_PATH_WITH_ID_WITHDRAW, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isAccepted());

        Mockito.verify(accountService, Mockito.times(1)).withdrawFunds(id, request.funds());
    }

    @Test
    void withdrawFundsWithNegativeValueTest() throws Exception {
        var id = 1L;
        var request = new FundsRequest(BigDecimal.valueOf(-10L));

        mockMvc.perform(patch(ACCOUNT_PATH_WITH_ID_WITHDRAW, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());

        Mockito.verify(accountService, Mockito.times(0)).depositFunds(id, request.funds());
    }

    @Test
    void withdrawFundsWithExceptionTest() throws Exception {
        var id = 1L;
        var request = new FundsRequest(BigDecimal.TEN);

        doThrow(new InsufficientFundsException(INSUFFICIENT_FUNDS_MSG))
                .when(accountService).withdrawFunds(id, request.funds());

        mockMvc.perform(patch(ACCOUNT_PATH_WITH_ID_WITHDRAW, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(INSUFFICIENT_FUNDS_MSG));
    }

    @Test
    void transferFundsTest() throws Exception {
        var request = new TransferFundsRequest(1L, 2L, BigDecimal.TEN);

        mockMvc.perform(post(ACCOUNT_PATH_TRANSFER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isAccepted());

        Mockito.verify(accountService, Mockito.times(1)).transferFunds(request);
    }

    @Test
    void transferFundsWithSameSourceIdsTest() throws Exception {
        var request = new TransferFundsRequest(2L, 2L, BigDecimal.TEN);

        mockMvc.perform(post(ACCOUNT_PATH_TRANSFER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());

        Mockito.verify(accountService, Mockito.times(0)).transferFunds(request);
    }

    @Test
    void transferFundsWithInsufficientExceptionTest() throws Exception {
        var request = new TransferFundsRequest(1L, 2L, BigDecimal.TEN);

        doThrow(new InsufficientFundsException(INSUFFICIENT_FUNDS_MSG))
                .when(accountService).transferFunds(request);

        mockMvc.perform(post(ACCOUNT_PATH_TRANSFER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(INSUFFICIENT_FUNDS_MSG));

        Mockito.verify(accountService, Mockito.times(1)).transferFunds(request);
    }
}