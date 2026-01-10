package com.mini.project.financial_tracker.controller;

import com.mini.project.financial_tracker.dto.request.TransactionRequest;
import com.mini.project.financial_tracker.dto.response.DataResponse;
import com.mini.project.financial_tracker.dto.response.MessageResponse;
import com.mini.project.financial_tracker.dto.response.TransactionResponse;
import com.mini.project.financial_tracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<DataResponse<TransactionResponse>> createTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.ok(new DataResponse<>(200, "Transaction created successfully", response));
    }

    @GetMapping
    public ResponseEntity<DataResponse<List<TransactionResponse>>> getAllTransactions() {
        List<TransactionResponse> response = transactionService.getAllTransactions();
        return ResponseEntity.ok(new DataResponse<>(200, "Transactions retrieved successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<TransactionResponse>> getTransactionById(@PathVariable UUID id) {
        TransactionResponse response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(new DataResponse<>(200, "Transaction retrieved successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResponse<TransactionResponse>> updateTransaction(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.updateTransaction(id, request);
        return ResponseEntity.ok(new DataResponse<>(200, "Transaction updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse<String>> deleteTransaction(@PathVariable UUID id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(new MessageResponse<>(200, "Transaction deleted successfully"));
    }
}
