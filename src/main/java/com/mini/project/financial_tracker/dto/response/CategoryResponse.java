package com.mini.project.financial_tracker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

import com.mini.project.financial_tracker.utils.enums.CategoryType;

import java.io.Serializable;

@Data
@Builder
public class CategoryResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private CategoryType type;
}
