package com.mini.project.financial_tracker.service;

import com.mini.project.financial_tracker.dto.request.CategoryRequest;
import com.mini.project.financial_tracker.dto.response.CategoryResponse;
import com.mini.project.financial_tracker.entity.Category;
import com.mini.project.financial_tracker.repository.CategoryRepository;
import com.mini.project.financial_tracker.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.findByName(request.getName().toLowerCase()).isPresent()) {
            throw new BadRequestException("Category already exists");
        }

        Category category = new Category();
        category.setName(request.getName().toLowerCase());
        category.setType(request.getType());

        Category savedCategory = categoryRepository.save(category);
        log.info("Created category: {}", savedCategory.getId());

        return mapToResponse(savedCategory);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#root.methodName")
    public List<CategoryResponse> getAllCategories() {

        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(request.getName());
        category.setType(request.getType());

        Category updatedCategory = categoryRepository.save(category);
        log.info("Updated category: {}", updatedCategory.getId());

        return mapToResponse(updatedCategory);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categoryRepository.delete(category);
        log.info("Deleted category: {}", id);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .build();
    }
}
