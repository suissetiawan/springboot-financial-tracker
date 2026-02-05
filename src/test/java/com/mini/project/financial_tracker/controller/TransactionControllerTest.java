package com.mini.project.financial_tracker.controller;

import com.mini.project.financial_tracker.dto.request.TransactionRequest;
import com.mini.project.financial_tracker.dto.response.DataResponse;
import com.mini.project.financial_tracker.dto.response.TransactionResponse;
import com.mini.project.financial_tracker.dto.response.TransactionDetailResponse;
import com.mini.project.financial_tracker.service.TransactionService;
import com.mini.project.financial_tracker.util.helper.SecurityUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    void createTransaction_ShouldReturnResponse() {
        TransactionRequest request = new TransactionRequest();
        TransactionResponse response = TransactionResponse.builder().build();
        
        when(transactionService.createTransaction(any())).thenReturn(response);
        
        ResponseEntity<DataResponse<TransactionResponse>> actual = transactionController.createTransaction(request);
        assertEquals(200, actual.getStatusCode().value());
        assertEquals("Transaction created successfully", actual.getBody().getMessage());
    }

    @Test
    void getAllTransactions_ShouldReturnResponse() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("user");
            when(transactionService.getAllTransactions("user")).thenReturn(List.of());
            
            ResponseEntity<DataResponse<List<TransactionResponse>>> actual = transactionController.getAllTransactions();
            assertEquals(200, actual.getStatusCode().value());
            assertEquals("Transactions retrieved successfully", actual.getBody().getMessage());
        }
    }

    @Test
    void getTransactionById_ShouldReturnResponse() {
        UUID id = UUID.randomUUID();
        when(transactionService.getTransactionById(id)).thenReturn(TransactionDetailResponse.builder().build());
        
        ResponseEntity<DataResponse<TransactionDetailResponse>> actual = transactionController.getTransactionById(id);
        assertEquals(200, actual.getStatusCode().value());
        assertEquals("Transaction retrieved successfully", actual.getBody().getMessage());
    }

    @Test
    void updateTransaction_ShouldReturnResponse() {
        UUID id = UUID.randomUUID();
        TransactionRequest request = new TransactionRequest();
        when(transactionService.updateTransaction(any(), any())).thenReturn(TransactionResponse.builder().build());
        
        ResponseEntity<DataResponse<TransactionResponse>> actual = transactionController.updateTransaction(id, request);
        assertEquals(200, actual.getStatusCode().value());
        assertEquals("Transaction updated successfully", actual.getBody().getMessage());
    }

    @Test
    void deleteTransaction_ShouldReturnResponse() {
        UUID id = UUID.randomUUID();
        doNothing().when(transactionService).deleteTransaction(any());
        
        ResponseEntity<HttpStatus> actual = transactionController.deleteTransaction(id);
        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
    }
}
