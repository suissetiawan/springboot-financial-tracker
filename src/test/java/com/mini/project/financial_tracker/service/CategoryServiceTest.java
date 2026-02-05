package com.mini.project.financial_tracker.service;

import com.mini.project.financial_tracker.dto.request.CategoryRequest;
import com.mini.project.financial_tracker.dto.response.CategoryResponse;
import com.mini.project.financial_tracker.entity.Category;
import com.mini.project.financial_tracker.exception.BadRequestException;
import com.mini.project.financial_tracker.exception.NotFoundException;
import com.mini.project.financial_tracker.repository.CategoryRepository;
import com.mini.project.financial_tracker.util.enums.CategoryType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_ShouldReturnCategoryResponse_WhenSuccess() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Food");
        request.setType(CategoryType.EXPENSE);

        when(categoryRepository.findByName("food")).thenReturn(Optional.empty());
        
        Category savedCategory = new Category();
        savedCategory.setId(UUID.randomUUID());
        savedCategory.setName("food");
        savedCategory.setType(CategoryType.EXPENSE);
        
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryResponse response = categoryService.createCategory(request);

        assertNotNull(response);
        assertEquals("food", response.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_ShouldThrowBadRequest_WhenAlreadyExists() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Food");

        when(categoryRepository.findByName("food")).thenReturn(Optional.of(new Category()));

        assertThrows(BadRequestException.class, () -> categoryService.createCategory(request));
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategoryResponse() {
        Category category = new Category();
        category.setName("food");
        category.setType(CategoryType.EXPENSE);

        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryResponse> responses = categoryService.getAllCategories();

        assertEquals(1, responses.size());
        assertEquals("food", responses.get(0).getName());
    }

    @Test
    void updateCategory_ShouldReturnCategoryResponse_WhenSuccess() {
        UUID id = UUID.randomUUID();
        CategoryRequest request = new CategoryRequest();
        request.setName("Drinks");
        request.setType(CategoryType.EXPENSE);

        Category existingCategory = new Category();
        existingCategory.setId(id);
        existingCategory.setName("food");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        CategoryResponse response = categoryService.updateCategory(id, request);

        assertNotNull(response);
        assertEquals("Drinks", response.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldThrowNotFound_WhenNotFound() {
        UUID id = UUID.randomUUID();
        CategoryRequest request = new CategoryRequest();

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.updateCategory(id, request));
    }

    @Test
    void deleteCategory_ShouldDelete_WhenSuccess() {
        UUID id = UUID.randomUUID();
        Category category = new Category();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(id);

        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategory_ShouldThrowNotFound_WhenNotFound() {
        UUID id = UUID.randomUUID();

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(id));
    }
}
