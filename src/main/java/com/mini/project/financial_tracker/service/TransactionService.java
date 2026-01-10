package com.mini.project.financial_tracker.service;

import com.mini.project.financial_tracker.dto.request.TransactionRequest;
import com.mini.project.financial_tracker.dto.response.TransactionResponse;
import com.mini.project.financial_tracker.entity.Category;
import com.mini.project.financial_tracker.entity.Transaction;
import com.mini.project.financial_tracker.entity.User;
import com.mini.project.financial_tracker.repository.CategoryRepository;
import com.mini.project.financial_tracker.repository.TransactionRepository;
import com.mini.project.financial_tracker.repository.UserRepository;
import com.mini.project.financial_tracker.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import com.mini.project.financial_tracker.exception.NotFoundException;
import java.time.LocalDateTime;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionResponse createTransaction(TransactionRequest request) {
        String username = SecurityUtils.getCurrentUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(LocalDateTime.now());
        transaction.setCategory(category);
        transaction.setUser(user);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Created transaction: {}", savedTransaction.getId());

        return mapToResponse(savedTransaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        String username = SecurityUtils.getCurrentUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Fetching transactions for user: {}", username);
        List<Transaction> transactions = transactionRepository.findAllByUser(user);

        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(UUID id) {
        String username = SecurityUtils.getCurrentUsername();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getEmail().equals(username)) {
            throw new RuntimeException("Access denied");
        }
        
        log.info("Fetching transaction detail: {}", id);
        return mapToResponse(transaction);
    }

    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionResponse updateTransaction(UUID id, TransactionRequest request) {
        String username = SecurityUtils.getCurrentUsername();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getEmail().equals(username)) {
            throw new RuntimeException("Access denied");
        }

        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        // transaction.setDate(request.getDate()); // Date is not in request
        transaction.setCategory(category);

        Transaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Updated transaction: {}", updatedTransaction.getId());

        return mapToResponse(updatedTransaction);
    }

    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public void deleteTransaction(UUID id) {
        String username = SecurityUtils.getCurrentUsername();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getEmail().equals(username)) {
            throw new RuntimeException("Access denied");
        }

        transactionRepository.delete(transaction);
        log.info("Deleted transaction: {}", id);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .date(transaction.getDate())
                .categoryName(transaction.getCategory().getName())
                .categoryType(String.valueOf(transaction.getCategory().getType()))
                .userId(transaction.getUser().getId())
                .build();
    }
}
