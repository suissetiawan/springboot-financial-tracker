package com.mini.project.financial_tracker.service;

import com.mini.project.financial_tracker.dto.request.LoginRequest;
import com.mini.project.financial_tracker.dto.request.RegisterRequest;
import com.mini.project.financial_tracker.dto.response.AuthResponse;
import com.mini.project.financial_tracker.dto.response.DataResponse;
import com.mini.project.financial_tracker.dto.response.MessageResponse;
import com.mini.project.financial_tracker.repository.UserRepository;
import com.mini.project.financial_tracker.util.helper.JwtUtils;
import com.mini.project.financial_tracker.util.helper.SecurityUtils;
import com.mini.project.financial_tracker.entity.User;
import com.mini.project.financial_tracker.entity.RefreshToken;
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

import com.mini.project.financial_tracker.repository.RefreshTokenRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.mini.project.financial_tracker.dto.request.RefreshTokenRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
    public RefreshTokenRepository refreshTokenRepository;

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

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setJti("jti");
        refreshToken.setUserId(existingUser.getId().toString());
        refreshToken.setRefreshToken("refreshToken");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);
        when(jwtUtil.generateAccessToken(existingUser)).thenReturn("jwtToken");
        when(jwtUtil.generateRefreshToken(existingUser)).thenReturn("refreshToken");

        // Execute
        ResponseEntity<DataResponse<AuthResponse>> response = authService.login(reqMock);

        // Verify
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingUser.getId().toString(), response.getBody().getResponse().getUserId());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(refreshTokenRepository, times(1)).save(any());
        verify(userRepository, times(1)).findByEmail(any());
        verify(jwtUtil, times(1)).generateAccessToken(existingUser);
        verify(jwtUtil, times(1)).generateRefreshToken(existingUser);
    }

    @Test
    void logout_ShouldLogoutSuccessfully_WhenTokenIsValid() {
        RefreshTokenRequest reqMock = new RefreshTokenRequest();
        reqMock.setRefreshToken("validRefreshToken");

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(jwtUtil.validateRefreshToken("validRefreshToken")).thenReturn(true);
            when(jwtUtil.extractJtiFromRefreshToken("validRefreshToken")).thenReturn("jti");
            when(refreshTokenRepository.findById("jti")).thenReturn(Optional.of(new RefreshToken()));

            // Execute
            ResponseEntity<MessageResponse<String>> response = authService.logout(reqMock);

            // Verify
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Logout successful", response.getBody().getMessage());
            verify(refreshTokenRepository, times(1)).deleteById("jti");
        }
    }

    @Test
    void logout_ShouldThrowBadRequest_WhenTokenIsInvalid() {
        RefreshTokenRequest reqMock = new RefreshTokenRequest();
        reqMock.setRefreshToken("invalidRefreshToken");

        when(jwtUtil.validateRefreshToken("invalidRefreshToken")).thenReturn(false);

        // Verify
        assertThrows(BadRequestException.class, () -> {
            authService.logout(reqMock);
        });
    }

    @Test
    void logout_ShouldThrowBadRequest_WhenUserNotLoggedIn() {
        RefreshTokenRequest reqMock = new RefreshTokenRequest();
        reqMock.setRefreshToken("validRefreshToken");

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(null);
            when(jwtUtil.validateRefreshToken("validRefreshToken")).thenReturn(true);

            // Verify
            assertThrows(BadRequestException.class, () -> {
                authService.logout(reqMock);
            });
        }
    }

    @Test
    void logout_ShouldThrowBadRequest_WhenTokenNotFoundInRepo() {
        RefreshTokenRequest reqMock = new RefreshTokenRequest();
        reqMock.setRefreshToken("validRefreshToken");

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(jwtUtil.validateRefreshToken("validRefreshToken")).thenReturn(true);
            when(jwtUtil.extractJtiFromRefreshToken("validRefreshToken")).thenReturn("jti");
            when(refreshTokenRepository.findById("jti")).thenReturn(Optional.empty());

            // Verify
            assertThrows(BadRequestException.class, () -> {
                authService.logout(reqMock);
            });
        }
    }

    @Test
    void refreshToken_ShouldReturnNewTokens_WhenValid() {
        RefreshTokenRequest reqMock = new RefreshTokenRequest();
        reqMock.setRefreshToken("validRefreshToken");

        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        RefreshToken storedToken = new RefreshToken();
        storedToken.setJti("jti");
        storedToken.setUserId(userId.toString());
        storedToken.setRefreshToken("validRefreshToken");

        when(jwtUtil.validateRefreshToken("validRefreshToken")).thenReturn(true);
        when(jwtUtil.extractJtiFromRefreshToken("validRefreshToken")).thenReturn("jti");
        when(jwtUtil.extractUserIdFromRefreshToken("validRefreshToken")).thenReturn(userId.toString());
        when(refreshTokenRepository.findById("jti")).thenReturn(Optional.of(storedToken));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(user)).thenReturn("newAccessToken");
        when(jwtUtil.generateRefreshToken(user)).thenReturn("newRefreshToken");
        when(jwtUtil.extractJtiFromRefreshToken("newRefreshToken")).thenReturn("newJti");

        // Execute
        ResponseEntity<DataResponse<AuthResponse>> response = authService.refreshToken(reqMock);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("newAccessToken", response.getBody().getResponse().getAccessToken());
        verify(refreshTokenRepository, times(1)).deleteById("jti");
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void refreshToken_ShouldThrowBadRequest_WhenTokenInvalid() {
        RefreshTokenRequest reqMock = new RefreshTokenRequest();
        reqMock.setRefreshToken("invalidRefreshToken");

        when(jwtUtil.validateRefreshToken("invalidRefreshToken")).thenReturn(false);

        // Verify
        assertThrows(BadRequestException.class, () -> {
            authService.refreshToken(reqMock);
        });
    }

    @Test
    void refreshToken_ShouldThrowNotFound_WhenTokenNotFoundInRepo() {
        RefreshTokenRequest reqMock = new RefreshTokenRequest();
        reqMock.setRefreshToken("validRefreshToken");

        when(jwtUtil.validateRefreshToken("validRefreshToken")).thenReturn(true);
        when(jwtUtil.extractJtiFromRefreshToken("validRefreshToken")).thenReturn("jti");
        when(refreshTokenRepository.findById("jti")).thenReturn(Optional.empty());

        // Verify
        assertThrows(NotFoundException.class, () -> {
            authService.refreshToken(reqMock);
        });
    }

    @Test
    void refreshToken_ShouldThrowBadRequest_WhenUserIdMismatch() {
        RefreshTokenRequest reqMock = new RefreshTokenRequest();
        reqMock.setRefreshToken("validRefreshToken");

        RefreshToken storedToken = new RefreshToken();
        storedToken.setUserId("differentUserId");

        when(jwtUtil.validateRefreshToken("validRefreshToken")).thenReturn(true);
        when(jwtUtil.extractJtiFromRefreshToken("validRefreshToken")).thenReturn("jti");
        when(jwtUtil.extractUserIdFromRefreshToken("validRefreshToken")).thenReturn("userId");
        when(refreshTokenRepository.findById("jti")).thenReturn(Optional.of(storedToken));

        // Verify
        assertThrows(BadRequestException.class, () -> {
            authService.refreshToken(reqMock);
        });
    }

    @Test
    void refreshToken_ShouldThrowBadRequest_WhenTokenMismatch() {
        RefreshTokenRequest reqMock = new RefreshTokenRequest();
        reqMock.setRefreshToken("validRefreshToken");

        RefreshToken storedToken = new RefreshToken();
        storedToken.setUserId("userId");
        storedToken.setRefreshToken("differentRefreshToken");

        when(jwtUtil.validateRefreshToken("validRefreshToken")).thenReturn(true);
        when(jwtUtil.extractJtiFromRefreshToken("validRefreshToken")).thenReturn("jti");
        when(jwtUtil.extractUserIdFromRefreshToken("validRefreshToken")).thenReturn("userId");
        when(refreshTokenRepository.findById("jti")).thenReturn(Optional.of(storedToken));

        // Verify
        assertThrows(BadRequestException.class, () -> {
            authService.refreshToken(reqMock);
        });
    }

    @Test
    void refreshToken_ShouldThrowNotFound_WhenUserNotFound() {
        RefreshTokenRequest reqMock = new RefreshTokenRequest();
        reqMock.setRefreshToken("validRefreshToken");

        UUID userId = UUID.randomUUID();
        RefreshToken storedToken = new RefreshToken();
        storedToken.setUserId(userId.toString());
        storedToken.setRefreshToken("validRefreshToken");

        when(jwtUtil.validateRefreshToken("validRefreshToken")).thenReturn(true);
        when(jwtUtil.extractJtiFromRefreshToken("validRefreshToken")).thenReturn("jti");
        when(jwtUtil.extractUserIdFromRefreshToken("validRefreshToken")).thenReturn(userId.toString());
        when(refreshTokenRepository.findById("jti")).thenReturn(Optional.of(storedToken));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Verify
        assertThrows(NotFoundException.class, () -> {
            authService.refreshToken(reqMock);
        });
    }
}

        
