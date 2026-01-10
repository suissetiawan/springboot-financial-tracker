package com.mini.project.financial_tracker.dto.response;

import com.mini.project.financial_tracker.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
public class UserResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private String email;
    private Role role;
}
