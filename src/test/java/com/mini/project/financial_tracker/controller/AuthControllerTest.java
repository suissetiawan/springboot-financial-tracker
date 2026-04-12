package com.mini.project.financial_tracker.controller;

import com.mini.project.financial_tracker.dto.request.LoginRequest;
import com.mini.project.financial_tracker.dto.request.RegisterRequest;
import com.mini.project.financial_tracker.dto.response.AuthResponse;
import com.mini.project.financial_tracker.dto.response.DataResponse;
import com.mini.project.financial_tracker.dto.response.MessageResponse;
import com.mini.project.financial_tracker.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_ShouldReturnResponse() {
        RegisterRequest request = new RegisterRequest();
        MessageResponse<String> body = new MessageResponse<>(201, "User registered successfully");
        ResponseEntity<MessageResponse<String>> expected = ResponseEntity.status(201).body(body);
        
        when(authService.register(any())).thenReturn(expected);
        
        ResponseEntity<MessageResponse<String>> actual = authController.register(request);
        assertEquals(expected, actual);
        assertEquals(201, actual.getBody().getStatus());
        assertEquals("User registered successfully", actual.getBody().getMessage());
    }

    @Test
    void login_ShouldReturnResponse() {
        LoginRequest request = new LoginRequest();
        AuthResponse authResponse = new AuthResponse("user-id", "access-token");
        DataResponse<AuthResponse> body = new DataResponse<>(200, "Login successful", authResponse);
        ResponseEntity<DataResponse<AuthResponse>> expected = ResponseEntity.ok(body);
        
        when(authService.login(any())).thenReturn(expected);
        
        ResponseEntity<DataResponse<AuthResponse>> actual = authController.login(request);
        assertEquals(expected, actual);
        assertEquals(200, actual.getBody().getStatus());
        assertEquals("Login successful", actual.getBody().getMessage());
        assertEquals("user-id", actual.getBody().getResponse().getUserId());
    }


    @Test
    void logout_ShouldReturnResponse() {
        ResponseEntity<MessageResponse<String>> expected = ResponseEntity.ok(new MessageResponse<>(200, "Success"));
        
        when(authService.logout()).thenReturn(expected);
        
        ResponseEntity<MessageResponse<String>> actual = authController.logout();
        assertEquals(expected, actual);
    }
}
