package com.mini.project.financial_tracker.service;

import com.mini.project.financial_tracker.dto.request.TransactionRequest;
import com.mini.project.financial_tracker.dto.response.TransactionDetailResponse;
import com.mini.project.financial_tracker.dto.response.TransactionResponse;
import com.mini.project.financial_tracker.entity.Category;
import com.mini.project.financial_tracker.entity.Transaction;
import com.mini.project.financial_tracker.entity.User;
import com.mini.project.financial_tracker.exception.NotFoundException;
import com.mini.project.financial_tracker.repository.CategoryRepository;
import com.mini.project.financial_tracker.repository.TransactionRepository;
import com.mini.project.financial_tracker.repository.UserRepository;
import com.mini.project.financial_tracker.util.enums.CategoryType;
import com.mini.project.financial_tracker.util.helper.SecurityUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createTransaction_ShouldReturnTransactionResponse_WhenSuccess() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(100.0);
        request.setDescription("Dinner");
        request.setCategory("Food");

        User user = new User();
        user.setEmail("test@example.com");

        Category category = new Category();
        category.setName("Food");
        category.setType(CategoryType.EXPENSE);

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(100.0);
        transaction.setDescription("Dinner");
        transaction.setCategory(category);
        transaction.setUser(user);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(categoryRepository.findByName("Food")).thenReturn(Optional.of(category));
            when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

            TransactionResponse response = transactionService.createTransaction(request);

            assertNotNull(response);
            assertEquals("Food", response.getCategoryName());
            verify(transactionRepository, times(1)).save(any(Transaction.class));
        }
    }

    @Test
    void createTransaction_ShouldThrowNotFound_WhenUserNotFound() {
        TransactionRequest request = new TransactionRequest();

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> transactionService.createTransaction(request));
        }
    }

    @Test
    void createTransaction_ShouldThrowNotFound_WhenCategoryNotFound() {
        TransactionRequest request = new TransactionRequest();
        request.setCategory("Food");

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
            when(categoryRepository.findByName("Food")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> transactionService.createTransaction(request));
        }
    }

    @Test
    void getAllTransactions_ShouldReturnListOfTransactionResponse() {
        String username = "test@example.com";
        User user = new User();
        user.setEmail(username);

        Category category = new Category();
        category.setName("Food");
        category.setType(CategoryType.EXPENSE);

        Transaction transaction = new Transaction();
        transaction.setCategory(category);
        transaction.setUser(user);

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
        when(transactionRepository.findAllByUserWithCategory(user)).thenReturn(List.of(transaction));

        List<TransactionResponse> responses = transactionService.getAllTransactions(username);

        assertEquals(1, responses.size());
        verify(transactionRepository, times(1)).findAllByUserWithCategory(user);
    }

    @Test
    void getAllTransactions_ShouldThrowNotFound_WhenUserNotFound() {
        String username = "test@example.com";
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> transactionService.getAllTransactions(username));
    }

    @Test
    void getTransactionById_ShouldReturnDetailResponse_WhenSuccess() {
        UUID id = UUID.randomUUID();
        String username = "test@example.com";
        User user = new User();
        user.setEmail(username);

        Category category = new Category();
        category.setName("Food");
        category.setType(CategoryType.EXPENSE);

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setUser(user);
        transaction.setCategory(category);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(username);
            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

            TransactionDetailResponse response = transactionService.getTransactionById(id);

            assertNotNull(response);
            assertEquals(username, transaction.getUser().getEmail());
        }
    }

    @Test
    void getTransactionById_ShouldThrowNotFound_WhenTransactionNotFound() {
        UUID id = UUID.randomUUID();
        String username = "test@example.com";
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(username);
            when(transactionRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> transactionService.getTransactionById(id));
        }
    }

    @Test
    void getTransactionById_ShouldThrowAccessDenied_WhenNotOwner() {
        UUID id = UUID.randomUUID();
        String username = "owner@example.com";
        String intruder = "intruder@example.com";

        User owner = new User();
        owner.setEmail(username);

        Transaction transaction = new Transaction();
        transaction.setUser(owner);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(intruder);
            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

            assertThrows(AccessDeniedException.class, () -> transactionService.getTransactionById(id));
        }
    }

    @Test
    void updateTransaction_ShouldReturnResponse_WhenSuccess() {
        UUID id = UUID.randomUUID();
        String username = "test@example.com";
        TransactionRequest request = new TransactionRequest();
        request.setAmount(200.0);
        request.setCategory("Bills");

        User user = new User();
        user.setEmail(username);

        Category oldCategory = new Category();
        oldCategory.setName("Food");

        Category newCategory = new Category();
        newCategory.setName("Bills");
        newCategory.setType(CategoryType.EXPENSE);

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setUser(user);
        transaction.setCategory(oldCategory);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(username);
            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
            when(categoryRepository.findByName("Bills")).thenReturn(Optional.of(newCategory));
            when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

            TransactionResponse response = transactionService.updateTransaction(id, request);

            assertNotNull(response);
            assertEquals("Bills", response.getCategoryName());
        }
    }

    @Test
    void updateTransaction_ShouldThrowNotFound_WhenTransactionNotFound() {
        UUID id = UUID.randomUUID();
        String username = "test@example.com";
        TransactionRequest request = new TransactionRequest();
        request.setAmount(200.0);
        request.setCategory("Bills");

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(username);
            when(transactionRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> transactionService.updateTransaction(id, request));
        }
    }

    @Test
    void updateTransaction_ShouldThrowNotFound_WhenCategoryNotFound() {
        UUID id = UUID.randomUUID();
        String username = "test@example.com";

        User user = new User();
        user.setEmail(username);

        Transaction transaction = new Transaction();
        transaction.setUser(user);

        TransactionRequest request = new TransactionRequest();
        request.setAmount(200.0);
        request.setCategory("Bills");

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(username);
            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
            when(categoryRepository.findByName("Bills")).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> transactionService.updateTransaction(id, request));
        }
    }

    @Test
    void updateTransaction_ShouldThrowAccessDenied_WhenNotOwner() {
        UUID id = UUID.randomUUID();
        String username = "owner@example.com";
        String intruder = "intruder@example.com";

        User owner = new User();
        owner.setEmail(username);

        Transaction transaction = new Transaction();
        transaction.setUser(owner);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(intruder);
            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

            assertThrows(AccessDeniedException.class, () -> transactionService.updateTransaction(id, new TransactionRequest()));
        }
    }

    @Test
    void deleteTransaction_ShouldDelete_WhenSuccess() {
        UUID id = UUID.randomUUID();
        String username = "test@example.com";
        User user = new User();
        user.setEmail(username);

        Transaction transaction = new Transaction();
        transaction.setUser(user);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(username);
            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

            transactionService.deleteTransaction(id);

            verify(transactionRepository, times(1)).delete(transaction);
        }
    }

    @Test
    void deleteTransaction_ShouldThrowNotFound_WhenTransactionNotFound() {
        UUID id = UUID.randomUUID();
        String username = "test@example.com";
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(username);
            when(transactionRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> transactionService.deleteTransaction(id));
        }
    }

    @Test
    void deleteTransaction_ShouldThrowAccessDenied_WhenNotOwner() {
        UUID id = UUID.randomUUID();
        String username = "owner@example.com";
        String intruder = "intruder@example.com";

        User owner = new User();
        owner.setEmail(username);

        Transaction transaction = new Transaction();
        transaction.setUser(owner);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(intruder);
            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

            assertThrows(AccessDeniedException.class, () -> transactionService.deleteTransaction(id));
        }
    }   
}
