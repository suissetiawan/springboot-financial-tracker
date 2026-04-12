package com.mini.project.financial_tracker.service;

import com.mini.project.financial_tracker.dto.request.LoginRequest;
import com.mini.project.financial_tracker.dto.request.RegisterRequest;
import com.mini.project.financial_tracker.dto.response.AuthResponse;
import com.mini.project.financial_tracker.dto.response.MessageResponse;
import com.mini.project.financial_tracker.dto.response.DataResponse;
import com.mini.project.financial_tracker.entity.User;
import com.mini.project.financial_tracker.exception.BadRequestException;
import com.mini.project.financial_tracker.exception.NotFoundException;
import com.mini.project.financial_tracker.repository.UserRepository;
import com.mini.project.financial_tracker.util.enums.Role;
import com.mini.project.financial_tracker.util.helper.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtil;
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
        
        return ResponseEntity.ok(new DataResponse<>(
            HttpStatus.OK.value(), 
            "Login successful", 
            new AuthResponse(user.getId().toString(), accessToken)));
    }

    public ResponseEntity<MessageResponse<String>> logout() {
        log.info("User logged out successfully");
        return ResponseEntity.ok(new MessageResponse<>(
            HttpStatus.OK.value(), 
            "Logout successful"));
    }

}
