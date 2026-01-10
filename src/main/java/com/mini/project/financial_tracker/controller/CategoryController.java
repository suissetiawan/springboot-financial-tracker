package com.mini.project.financial_tracker.controller;

import com.mini.project.financial_tracker.dto.request.CategoryRequest;
import com.mini.project.financial_tracker.dto.response.DataResponse;
import com.mini.project.financial_tracker.dto.response.MessageResponse;
import com.mini.project.financial_tracker.dto.response.CategoryResponse;
import com.mini.project.financial_tracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DataResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new DataResponse<>(HttpStatus.CREATED.value(), "success create category", response));
    }

    @GetMapping
    public ResponseEntity<DataResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> response = categoryService.getAllCategories();
        return ResponseEntity.ok(new DataResponse<>(
            HttpStatus.OK.value(), "success retrieve categories", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DataResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(new DataResponse<>(
            HttpStatus.OK.value(), "success update category", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse<String>> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new MessageResponse<>(
            HttpStatus.OK.value(), "success delete category"));
    }
}
