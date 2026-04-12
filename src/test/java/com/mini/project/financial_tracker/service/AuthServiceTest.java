package com.mini.project.financial_tracker.service;

import com.mini.project.financial_tracker.dto.request.LoginRequest;
import com.mini.project.financial_tracker.dto.request.RegisterRequest;
import com.mini.project.financial_tracker.dto.response.AuthResponse;
import com.mini.project.financial_tracker.dto.response.DataResponse;
import com.mini.project.financial_tracker.dto.response.MessageResponse;
import com.mini.project.financial_tracker.repository.UserRepository;
import com.mini.project.financial_tracker.util.helper.JwtUtils;
import com.mini.project.financial_tracker.entity.User;
import com.mini.project.financial_tracker.exception.BadRequestException;
import com.mini.project.financial_tracker.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    public AuthService authService;

    @Mock
    public UserRepository userRepository;

    @Mock
    public PasswordEncoder passwordEncoder;

    @Mock
    public JwtUtils jwtUtil;


    @Mock
    public AuthenticationManager authenticationManager;



    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        RegisterRequest reqMock = new RegisterRequest();
        reqMock.setEmail("test@example.com");

        User existingUser = new User();
        existingUser.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        // Verify
        assertThrows(BadRequestException.class, () -> {
            authService.register(reqMock);
        });
    }

    @Test
    void register_ShouldThrowException_WhenRoleIsInvalid() {
        RegisterRequest reqMock = new RegisterRequest();
        reqMock.setName("test");
        reqMock.setEmail("test@example.com");
        reqMock.setRole("invalid");
        reqMock.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Verify
        assertThrows(BadRequestException.class, () -> {
            authService.register(reqMock);
        });
    }

    @Test
    void register_ShouldRegisterAdmin_WhenRequestDataIsValid() {
        RegisterRequest reqMock = new RegisterRequest();
        reqMock.setName("test");
        reqMock.setEmail("test@example.com");
        reqMock.setRole("ADMIN");
        reqMock.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        
        // Execute
        ResponseEntity<MessageResponse <String>> response = authService.register(reqMock);

        // Verify
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldRegisterUser_WhenRequestDataIsValid() {
        RegisterRequest reqMock = new RegisterRequest();
        reqMock.setName("test");
        reqMock.setEmail("test@example.com");
        reqMock.setRole("USER");
        reqMock.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        
        // Execute
        ResponseEntity<MessageResponse <String>> response = authService.register(reqMock);

        // Verify
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_ShouldThrowException_WhenEmailDoesNotExist() {
        LoginRequest reqMock = new LoginRequest();
        reqMock.setEmail("test@example.com");
        reqMock.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Verify
        assertThrows(NotFoundException.class, () -> {
            authService.login(reqMock);
        });    
    }

    @Test
    void login_ShouldLoginUser_WhenRequestDataIsValid() {
        LoginRequest reqMock = new LoginRequest();
        reqMock.setEmail("test@example.com");
        reqMock.setPassword("password");

        User existingUser = new User();
        existingUser.setName("test");
        existingUser.setId(UUID.randomUUID());
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(jwtUtil.generateAccessToken(existingUser)).thenReturn("jwtToken");

        // Execute
        ResponseEntity<DataResponse<AuthResponse>> response = authService.login(reqMock);

        // Verify
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingUser.getId().toString(), response.getBody().getResponse().getUserId());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(any());
        verify(jwtUtil, times(1)).generateAccessToken(existingUser);
    }

    @Test
    void logout_ShouldLogoutSuccessfully() {
        // Execute
        ResponseEntity<MessageResponse<String>> response = authService.logout();

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logout successful", response.getBody().getMessage());
    }
}

        
