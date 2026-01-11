package com.mini.project.financial_tracker.service;

import com.mini.project.financial_tracker.dto.request.LoginRequest;
import com.mini.project.financial_tracker.dto.request.RefreshTokenRequest;
import com.mini.project.financial_tracker.dto.request.RegisterRequest;
import com.mini.project.financial_tracker.dto.response.AuthResponse;
import com.mini.project.financial_tracker.dto.response.MessageResponse;
import com.mini.project.financial_tracker.dto.response.DataResponse;
import com.mini.project.financial_tracker.entity.RefreshToken;
import com.mini.project.financial_tracker.entity.User;
import com.mini.project.financial_tracker.exception.BadRequestException;
import com.mini.project.financial_tracker.exception.NotFoundException;
import com.mini.project.financial_tracker.repository.UserRepository;
import com.mini.project.financial_tracker.utils.JwtUtils;
import com.mini.project.financial_tracker.utils.SecurityUtils;
import com.mini.project.financial_tracker.utils.enums.Role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mini.project.financial_tracker.repository.RefreshTokenRepository;
import java.util.UUID;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;


    public ResponseEntity<MessageResponse<String>> register(RegisterRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            log.info("User already exists: {}", request.getEmail());
            throw new BadRequestException("Email already in use");
        });

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        switch (request.getRole().toLowerCase()) {
                case "admin":
                        user.setRole(Role.ADMIN);
                        break;
                case "user":
                        user.setRole(Role.USER);
                        break;
                default:
                        throw new BadRequestException("use role admin or user");
        }

        userRepository.save(user);

        log.info("User registered successfully: {}", request.getEmail());

        return ResponseEntity.created(null)
                .body(new MessageResponse<>(
                        HttpStatus.CREATED.value(), 
                        "User registered successfully"));
    }

    public ResponseEntity<DataResponse<AuthResponse>> login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> {
            log.info("User not found: {}", request.getEmail());
            throw new NotFoundException("User not found");
        });

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // Simpan refresh token di redis
        refreshTokenRepository.save(
                new RefreshToken(
                        jwtUtil.extractJtiFromRefreshToken(refreshToken),
                        user.getId().toString(),
                        refreshToken
                )
        );

        log.info("User logged in successfully: {}", request.getEmail());
        return ResponseEntity.ok(new DataResponse<>(
            HttpStatus.OK.value(), 
            "Login successful", 
            new AuthResponse(user.getId().toString(), accessToken, refreshToken)));
    }

    public ResponseEntity<MessageResponse<String>> logout(RefreshTokenRequest request) {
        
        boolean isValid = jwtUtil.validateRefreshToken(request.getRefreshToken());
        String username = SecurityUtils.getCurrentUsername();

        if (!isValid || username == null) {
            log.info("Invalid refresh token");
            throw new BadRequestException("Please login first");
        }

        // Hapus refresh token di redis
        String jti = jwtUtil.extractJtiFromRefreshToken(request.getRefreshToken());
        refreshTokenRepository.findById(jti).orElseThrow(() -> {
            log.info("Refresh token not found");
            throw new BadRequestException("Please login first");
        });

        refreshTokenRepository.deleteById(jti);

        log.info("User logged out successfully");
        return ResponseEntity.ok(new MessageResponse<>(
            HttpStatus.OK.value(), 
            "Logout successful"));
    }

    public ResponseEntity<DataResponse<AuthResponse>> refreshToken(RefreshTokenRequest request) {

        // Validate refresh token
        if (!jwtUtil.validateRefreshToken(request.getRefreshToken())) {
            throw new BadRequestException("Invalid refresh token");
        }

        String jti = jwtUtil.extractJtiFromRefreshToken(request.getRefreshToken());
        String userId = jwtUtil.extractUserIdFromRefreshToken(request.getRefreshToken());

        // Check refresh token di redis
        RefreshToken storedToken = refreshTokenRepository.findById(jti)
                .orElseThrow(() -> new NotFoundException("Refresh token not found"));

        // Check user id di redis
        if (!storedToken.getUserId().equals(userId)) {
            throw new BadRequestException("Invalid refresh token");
        }

        // Check refresh token di redis
        if (!storedToken.getRefreshToken().equals(request.getRefreshToken())) {
            throw new BadRequestException("Refresh token mismatch");
        }

        // Ambil data user
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Hapus refresh token di redis
        refreshTokenRepository.deleteById(jti);

        // Generate new access token
        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        // Ambil jti dari new refresh token
        String newJti = jwtUtil.extractJtiFromRefreshToken(newRefreshToken);

        // Simpan new refresh token di redis
        refreshTokenRepository.save(
                new RefreshToken(
                        newJti,
                        userId,
                        newRefreshToken
                )
        );

        return ResponseEntity.ok(new DataResponse<>(
            HttpStatus.OK.value(), 
            "Success Generate New Access Token", 
            new AuthResponse(user.getId().toString(), newAccessToken, newRefreshToken)));
    }
}
