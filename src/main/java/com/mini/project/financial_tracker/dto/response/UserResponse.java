package com.mini.project.financial_tracker.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

import com.mini.project.financial_tracker.utils.enums.Role;

@Data
@Builder
public class UserResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private String email;
    private Role role;
}
