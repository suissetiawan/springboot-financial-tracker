package com.mini.project.financial_tracker.service;

import com.mini.project.financial_tracker.dto.request.TransactionRequest;
import com.mini.project.financial_tracker.dto.response.TransactionDetailResponse;
import com.mini.project.financial_tracker.dto.response.TransactionResponse;
import com.mini.project.financial_tracker.entity.Category;
import com.mini.project.financial_tracker.entity.Transaction;
import com.mini.project.financial_tracker.entity.User;
import com.mini.project.financial_tracker.repository.CategoryRepository;
import com.mini.project.financial_tracker.repository.TransactionRepository;
import com.mini.project.financial_tracker.repository.UserRepository;
import com.mini.project.financial_tracker.utils.SecurityUtils;
import com.mini.project.financial_tracker.utils.CacheConstants;

import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import com.mini.project.financial_tracker.exception.NotFoundException;
import org.springframework.security.access.AccessDeniedException;


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
    @CacheEvict(value = {CacheConstants.TRANSACTIONS, CacheConstants.SUMMARY}, allEntries = true)
    public TransactionResponse createTransaction(TransactionRequest request) {
        String username = SecurityUtils.getCurrentUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setCategory(category);
        transaction.setUser(user);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Created transaction: {}", savedTransaction.getId());

        return mapToResponse(savedTransaction);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.TRANSACTIONS, key = "#username")
    public List<TransactionResponse> getAllTransactions(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        log.info("Fetching transactions for user: {}", username);
        List<Transaction> transactions = transactionRepository.findAllByUserWithCategory(user);

        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.TRANSACTIONS, key = "#id")
    public TransactionDetailResponse getTransactionById(UUID id) {
        String username = SecurityUtils.getCurrentUsername();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (!transaction.getUser().getEmail().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }
        
        log.info("Fetching transaction detail: {}", id);
        return mapToDetailResponse(transaction);
    }

    @Transactional
    @CacheEvict(value = {CacheConstants.TRANSACTIONS, CacheConstants.SUMMARY}, allEntries = true)
    public TransactionResponse updateTransaction(UUID id, TransactionRequest request) {
        String username = SecurityUtils.getCurrentUsername();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (!transaction.getUser().getEmail().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }

        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setCategory(category);

        Transaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Updated transaction: {}", updatedTransaction.getId());

        return mapToResponse(updatedTransaction);
    }

    @Transactional
    @CacheEvict(value = {CacheConstants.TRANSACTIONS, CacheConstants.SUMMARY}, allEntries = true)
    public void deleteTransaction(UUID id) {
        String username = SecurityUtils.getCurrentUsername();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (!transaction.getUser().getEmail().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }

        transactionRepository.delete(transaction);
        log.info("Deleted transaction: {}", id);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .categoryName(transaction.getCategory().getName())
                .categoryType(String.valueOf(transaction.getCategory().getType()))
                .build();
    }

    private TransactionDetailResponse mapToDetailResponse(Transaction transaction) {
        return TransactionDetailResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .updatedAt(transaction.getUpdatedAt())
                .categoryName(transaction.getCategory().getName())
                .categoryType(String.valueOf(transaction.getCategory().getType()))
                .userId(transaction.getUser().getId())
                .build();
    }
}
