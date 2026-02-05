package com.mini.project.financial_tracker.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mini.project.financial_tracker.dto.response.DataResponse;
import com.mini.project.financial_tracker.dto.response.UserResponse;
import com.mini.project.financial_tracker.service.UserService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers_ShouldReturnResponse() {
        List<UserResponse> response = List.of(UserResponse.builder().build());
        ResponseEntity<DataResponse<List<UserResponse>>> expected = ResponseEntity
            .status(HttpStatus.OK)
            .body(new DataResponse<>(HttpStatus.OK.value(), "success retrieve users", response));
                
        when(userService.getAllUsers()).thenReturn(response);
        ResponseEntity<DataResponse<List<UserResponse>>> actual = userController.getAllUsers();
        assertEquals(expected, actual);
    }

    @Test
    void getUserById_ShouldReturnResponse() {
        UUID id = UUID.randomUUID();
        UserResponse response = UserResponse.builder().build();
        ResponseEntity<DataResponse<UserResponse>> expected = ResponseEntity
            .status(HttpStatus.OK)
            .body(new DataResponse<>(HttpStatus.OK.value(), "success retrieve user", response));
                
        when(userService.getUserById(id)).thenReturn(response);
        ResponseEntity<DataResponse<UserResponse>> actual = userController.getUserById(id);
        assertEquals(expected, actual);
    }
}
