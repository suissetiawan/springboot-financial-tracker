package com.mini.project.financial_tracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GreetingController {

    @GetMapping("/")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Financial Tracker API");
        response.put("status", "Running");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("Authentication", "/auth/** (login, register, refresh, logout)");
        endpoints.put("Transactions", "/api/transactions");
        endpoints.put("Categories", "/api/categories");
        endpoints.put("Summary", "/api/summary");
        endpoints.put("Users", "/api/users (Admin only)");
        
        response.put("endpoints", endpoints);
        response.put("documentation", "Refer to README.md or project documentation for details");
        
        return response;
    }
}
