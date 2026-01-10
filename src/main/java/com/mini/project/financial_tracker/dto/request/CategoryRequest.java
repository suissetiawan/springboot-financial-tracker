package com.mini.project.financial_tracker.dto.request;

import com.mini.project.financial_tracker.entity.CategoryType;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Data
public class CategoryRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Category name is required")
    private String name;

    @NotNull(message = "Category type is required")
    private CategoryType type;
}
