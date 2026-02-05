package com.mini.project.financial_tracker.service;

import com.mini.project.financial_tracker.dto.response.UserResponse;
import com.mini.project.financial_tracker.entity.User;
import com.mini.project.financial_tracker.exception.BadRequestException;
import com.mini.project.financial_tracker.exception.NotFoundException;
import com.mini.project.financial_tracker.repository.UserRepository;
import com.mini.project.financial_tracker.util.enums.Role;
import com.mini.project.financial_tracker.util.helper.SecurityUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_ShouldReturnListOfUserResponse() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> responses = userService.getAllUsers();

        assertEquals(1, responses.size());
        assertEquals(user.getEmail(), responses.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenOwnProfile() {
        UUID userId = UUID.randomUUID();
        String username = "test@example.com";

        User user = new User();
        user.setId(userId);
        user.setEmail(username);
        user.setRole(Role.USER);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(username);
            when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            UserResponse response = userService.getUserById(userId);

            assertNotNull(response);
            assertEquals(username, response.getEmail());
        }
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenAdminAccess() {
        UUID targetUserId = UUID.randomUUID();
        String adminUsername = "admin@example.com";

        User admin = new User();
        admin.setEmail(adminUsername);
        admin.setRole(Role.ADMIN);

        User targetUser = new User();
        targetUser.setId(targetUserId);
        targetUser.setEmail("user@example.com");
        targetUser.setRole(Role.USER);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(adminUsername);
            when(userRepository.findByEmail(adminUsername)).thenReturn(Optional.of(admin));
            when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

            UserResponse response = userService.getUserById(targetUserId);

            assertNotNull(response);
            assertEquals(targetUser.getEmail(), response.getEmail());
        }
    }

    @Test
    void getUserById_ShouldThrowNotFound_WhenCurrentUserNotFound() {
        UUID userId = UUID.randomUUID();
        String username = "test@example.com";

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(username);
            when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
        }
    }

    @Test
    void getUserById_ShouldThrowNotFound_WhenTargetUserNotFound() {
        UUID userId = UUID.randomUUID();
        String username = "test@example.com";

        User currentUser = new User();
        currentUser.setEmail(username);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(username);
            when(userRepository.findByEmail(username)).thenReturn(Optional.of(currentUser));
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
        }
    }

    @Test
    void getUserById_ShouldThrowBadRequest_WhenAccessDenied() {
        UUID targetUserId = UUID.randomUUID();
        String currentUsername = "user1@example.com";

        User currentUser = new User();
        currentUser.setEmail(currentUsername);
        currentUser.setRole(Role.USER);

        User targetUser = new User();
        targetUser.setId(targetUserId);
        targetUser.setEmail("user2@example.com");
        targetUser.setRole(Role.USER);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(currentUsername);
            when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.of(currentUser));
            when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

            assertThrows(BadRequestException.class, () -> userService.getUserById(targetUserId));
        }
    }
}
