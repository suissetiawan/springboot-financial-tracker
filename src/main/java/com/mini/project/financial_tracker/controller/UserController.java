package com.mini.project.financial_tracker.controller;

import com.mini.project.financial_tracker.dto.response.DataResponse;
import com.mini.project.financial_tracker.dto.response.UserResponse;
import com.mini.project.financial_tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DataResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(new DataResponse<>(
            HttpStatus.OK.value(), "success retrieve users", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(new DataResponse<>(
            HttpStatus.OK.value(), "success retrieve user", user));
    }
}
