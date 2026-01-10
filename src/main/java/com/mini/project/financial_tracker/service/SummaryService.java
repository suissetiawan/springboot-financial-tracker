package com.mini.project.financial_tracker.service;

import com.mini.project.financial_tracker.dto.response.SummaryResponse;
import com.mini.project.financial_tracker.entity.CategoryType;
import com.mini.project.financial_tracker.entity.User;
import com.mini.project.financial_tracker.exception.NotFoundException;
import com.mini.project.financial_tracker.repository.TransactionRepository;
import com.mini.project.financial_tracker.repository.UserRepository;
import com.mini.project.financial_tracker.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SummaryService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "summary", key = "#root.methodName + '_' + T(com.mini.project.financial_tracker.utils.SecurityUtils).getCurrentUsername()")
    public SummaryResponse getSummary() {
         String username = SecurityUtils.getCurrentUsername();
         User user = userRepository.findByEmail(username)
                 .orElseThrow(() -> new NotFoundException("User not found"));
         
        log.info("Calculating summary for user: {}", user.getId());
        Double totalIncome = transactionRepository.sumAmountByUserIdAndCategoryType(user.getId(), CategoryType.INCOME);
        Double totalExpense = transactionRepository.sumAmountByUserIdAndCategoryType(user.getId(), CategoryType.EXPENSE);

        if (totalIncome == null) totalIncome = 0.0;
        if (totalExpense == null) totalExpense = 0.0;

        Double balance = totalIncome - totalExpense;

        return SummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .build();
    }
}
