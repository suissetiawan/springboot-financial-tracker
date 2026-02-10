package com.mini.project.financial_tracker.service;

import com.mini.project.financial_tracker.dto.response.SummaryResponse;
import com.mini.project.financial_tracker.entity.User;
import com.mini.project.financial_tracker.exception.NotFoundException;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SummaryServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SummaryService summaryService;

    @Test
    void getSummary_ShouldReturnCorrectBalance_WhenDataExists() {
        String username = "test@example.com";
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail(username);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(username);
            when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
            when(transactionRepository.sumAmountByUserIdAndCategoryType(userId, CategoryType.INCOME)).thenReturn(1000.0);
            when(transactionRepository.sumAmountByUserIdAndCategoryType(userId, CategoryType.EXPENSE)).thenReturn(400.0);

            SummaryResponse response = summaryService.getSummary();

            assertNotNull(response);
            assertEquals(1000.0, response.getTotalIncome());
            assertEquals(400.0, response.getTotalExpense());
            assertEquals(600.0, response.getBalance());
        }
    }

    @Test
    void getSummary_ShouldReturnZeros_WhenNoData() {
        String username = "test@example.com";
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail(username);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(username);
            when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
            when(transactionRepository.sumAmountByUserIdAndCategoryType(any(), any())).thenReturn(null);

            SummaryResponse response = summaryService.getSummary();

            assertNotNull(response);
            assertEquals(0.0, response.getTotalIncome());
            assertEquals(0.0, response.getTotalExpense());
            assertEquals(0.0, response.getBalance());
        }
    }

    @Test
    void getSummary_ShouldThrowNotFound_WhenUserNotFound() {
        String username = "test@example.com";

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(username);
            when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> summaryService.getSummary());
        }
    }
}
