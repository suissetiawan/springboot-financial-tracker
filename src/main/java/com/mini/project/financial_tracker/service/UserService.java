package com.mini.project.financial_tracker.service;

import com.mini.project.financial_tracker.dto.response.UserResponse;
import com.mini.project.financial_tracker.entity.User;
import com.mini.project.financial_tracker.entity.Role;
import com.mini.project.financial_tracker.exception.BadRequestException;
import com.mini.project.financial_tracker.exception.NotFoundException;
import com.mini.project.financial_tracker.repository.UserRepository;
import com.mini.project.financial_tracker.utils.SecurityUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Cacheable(value = "users", key = "#root.methodName")
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "users", key = "#root.methodName")
    public UserResponse getUserById(UUID id) {
        String username = SecurityUtils.getCurrentUsername();

        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!targetUser.getEmail().equals(username) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("Access denied");
        }

        return mapToResponse(targetUser);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
