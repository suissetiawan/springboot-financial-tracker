package com.mini.project.financial_tracker.integration;

import com.mini.project.financial_tracker.dto.request.CategoryRequest;
import com.mini.project.financial_tracker.util.enums.CategoryType;
import com.mini.project.financial_tracker.util.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryIntegrationTest extends BaseIntegrationTest {

    private String accessToken;
    private final String email = "category-test-" + System.currentTimeMillis() + "@example.com";
    private final String password = "password123";

    @BeforeEach
    void setUp() throws Exception {
        registerUser(email, password, Role.ADMIN);
        accessToken = getAccessToken(email, password);
    }

    @Test
    void categoryCRUD_ShouldSucceed() throws Exception {
        // 1. Create
        CategoryRequest request = new CategoryRequest();
        request.setName("Travel");
        request.setType(CategoryType.EXPENSE);

        mockMvc.perform(post("/api/categories")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.name").value("travel")); // Service lowercases the name

        // 2. Get All
        mockMvc.perform(get("/api/categories")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response[0].name").value("travel"));
    }
}
