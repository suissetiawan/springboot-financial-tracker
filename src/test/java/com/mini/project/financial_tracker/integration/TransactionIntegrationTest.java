package com.mini.project.financial_tracker.integration;

import com.mini.project.financial_tracker.dto.request.CategoryRequest;
import com.mini.project.financial_tracker.dto.request.TransactionRequest;
import com.mini.project.financial_tracker.util.enums.CategoryType;
import com.mini.project.financial_tracker.util.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionIntegrationTest extends BaseIntegrationTest {

    private String accessToken;
    private final String email = "transaction-test-" + System.currentTimeMillis() + "@example.com";
    private final String password = "password123";

    @BeforeEach
    void setUp() throws Exception {
        // Register as ADMIN to be able to create categories in setup
        registerUser(email, password, Role.ADMIN);
        accessToken = getAccessToken(email, password);

        CategoryRequest incomeCat = new CategoryRequest();
        incomeCat.setName("Bonus");
        incomeCat.setType(CategoryType.INCOME);

        mockMvc.perform(post("/api/categories")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incomeCat)))
                .andExpect(status().isCreated());

        CategoryRequest expenseCat = new CategoryRequest();
        expenseCat.setName("Coffee");
        expenseCat.setType(CategoryType.EXPENSE);

        mockMvc.perform(post("/api/categories")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseCat)))
                .andExpect(status().isCreated());
    }

    @Test
    void transactionFlow_AndSummary_ShouldSucceed() throws Exception {
        // 1. Create Income (using lowercase name as service lowercases them)
        TransactionRequest incomeRequest = new TransactionRequest();
        incomeRequest.setCategory("bonus");
        incomeRequest.setAmount(100.0);
        incomeRequest.setDescription("Monthly Bonus");

        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incomeRequest)))
                .andExpect(status().isOk());

        // 2. Create Expense
        TransactionRequest expenseRequest = new TransactionRequest();
        expenseRequest.setCategory("coffee");
        expenseRequest.setAmount(5.0);
        expenseRequest.setDescription("Morning Coffee");

        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequest)))
                .andExpect(status().isOk());

        // 3. Verify Summary
        mockMvc.perform(get("/api/summary")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.totalIncome").value(100.0))
                .andExpect(jsonPath("$.response.totalExpense").value(5.0))
                .andExpect(jsonPath("$.response.balance").value(95.0));
    }
}
