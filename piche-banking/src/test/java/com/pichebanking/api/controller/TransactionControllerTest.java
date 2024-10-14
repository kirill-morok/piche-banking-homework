package com.pichebanking.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pichebanking.api.dto.request.FundsRequest;
import com.pichebanking.api.dto.request.TransferFundsRequest;
import com.pichebanking.api.exception.GlobalExceptionHandler;
import com.pichebanking.exception.AccountNotFoundException;
import com.pichebanking.exception.InsufficientFundsException;
import com.pichebanking.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static com.pichebanking.util.constant.ExceptionMessage.ACCOUNT_NOT_FOUND_MSG;
import static com.pichebanking.util.constant.ExceptionMessage.INSUFFICIENT_FUNDS_MSG;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class TransactionControllerTest {

    private static final String TRANSACTION_PATH_WITH_ID_DEPOSIT = "/v1/transactions/accounts/{id}/deposit";
    private static final String TRANSACTION_PATH_WITH_ID_WITHDRAW = "/v1/transactions/accounts/{id}/withdraw";
    private static final String TRANSACTION_PATH_TRANSFER = "/v1/transactions";

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void depositFundsTest() throws Exception {
        var id = 1L;
        var request = new FundsRequest(BigDecimal.TEN);

        mockMvc.perform(patch(TRANSACTION_PATH_WITH_ID_DEPOSIT, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isAccepted());

        Mockito.verify(transactionService, Mockito.times(1)).depositFunds(id, request.funds());
    }

    @Test
    void depositFundsWithZeroTest() throws Exception {
        var id = 1L;
        var request = new FundsRequest(BigDecimal.ZERO);

        mockMvc.perform(patch(TRANSACTION_PATH_WITH_ID_DEPOSIT, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());

        Mockito.verify(transactionService, Mockito.times(0)).depositFunds(id, request.funds());
    }

    @Test
    void depositFundsWithExceptionTest() throws Exception {
        var id = 1L;
        var request = new FundsRequest(BigDecimal.TEN);

        doThrow(new AccountNotFoundException(ACCOUNT_NOT_FOUND_MSG))
                .when(transactionService).depositFunds(id, request.funds());

        mockMvc.perform(patch(TRANSACTION_PATH_WITH_ID_DEPOSIT, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(ACCOUNT_NOT_FOUND_MSG));
    }

    @Test
    void withdrawFundsTest() throws Exception {
        var id = 1L;
        var request = new FundsRequest(BigDecimal.TEN);

        mockMvc.perform(patch(TRANSACTION_PATH_WITH_ID_WITHDRAW, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isAccepted());

        Mockito.verify(transactionService, Mockito.times(1)).withdrawFunds(id, request.funds());
    }

    @Test
    void withdrawFundsWithNegativeValueTest() throws Exception {
        var id = 1L;
        var request = new FundsRequest(BigDecimal.valueOf(-10L));

        mockMvc.perform(patch(TRANSACTION_PATH_WITH_ID_WITHDRAW, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());

        Mockito.verify(transactionService, Mockito.times(0)).depositFunds(id, request.funds());
    }

    @Test
    void withdrawFundsWithExceptionTest() throws Exception {
        var id = 1L;
        var request = new FundsRequest(BigDecimal.TEN);

        doThrow(new InsufficientFundsException(INSUFFICIENT_FUNDS_MSG))
                .when(transactionService).withdrawFunds(id, request.funds());

        mockMvc.perform(patch(TRANSACTION_PATH_WITH_ID_WITHDRAW, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(INSUFFICIENT_FUNDS_MSG));
    }

    @Test
    void transferFundsTest() throws Exception {
        var request = new TransferFundsRequest(1L, 2L, BigDecimal.TEN);

        mockMvc.perform(post(TRANSACTION_PATH_TRANSFER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isAccepted());

        Mockito.verify(transactionService, Mockito.times(1)).transferFunds(request);
    }

    @Test
    void transferFundsWithSameSourceIdsTest() throws Exception {
        var request = new TransferFundsRequest(2L, 2L, BigDecimal.TEN);

        mockMvc.perform(post(TRANSACTION_PATH_TRANSFER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());

        Mockito.verify(transactionService, Mockito.times(0)).transferFunds(request);
    }

    @Test
    void transferFundsWithInsufficientExceptionTest() throws Exception {
        var request = new TransferFundsRequest(1L, 2L, BigDecimal.TEN);

        doThrow(new InsufficientFundsException(INSUFFICIENT_FUNDS_MSG))
                .when(transactionService).transferFunds(request);

        mockMvc.perform(post(TRANSACTION_PATH_TRANSFER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(INSUFFICIENT_FUNDS_MSG));

        Mockito.verify(transactionService, Mockito.times(1)).transferFunds(request);
    }
}