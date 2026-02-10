package com.mini.project.financial_tracker.controller;

import com.mini.project.financial_tracker.dto.request.CategoryRequest;
import com.mini.project.financial_tracker.dto.response.DataResponse;
import com.mini.project.financial_tracker.dto.response.CategoryResponse;
import com.mini.project.financial_tracker.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void createCategory_ShouldReturnResponse() {
        CategoryRequest request = new CategoryRequest();
        CategoryResponse response = CategoryResponse.builder().build();
                
        ResponseEntity<DataResponse<CategoryResponse>> expected = ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new DataResponse<>(HttpStatus.CREATED
                .value(), "success create category", response));
        
        when(categoryService.createCategory(any())).thenReturn(response);
        
        ResponseEntity<DataResponse<CategoryResponse>> actual = categoryController.createCategory(request);
        assertEquals(expected, actual);
    }

    @Test
    void getAllCategories_ShouldReturnResponse() {
        List<CategoryResponse> response = List.of(CategoryResponse.builder().build());
        ResponseEntity<DataResponse<List<CategoryResponse>>> expected = ResponseEntity
            .status(HttpStatus.OK)
            .body(new DataResponse<>(HttpStatus.OK.value(), "success retrieve categories", response));
                
        when(categoryService.getAllCategories()).thenReturn(response);
        ResponseEntity<DataResponse<List<CategoryResponse>>> actual = categoryController.getAllCategories();
        assertEquals(expected, actual);
    }

    @Test
    void updateCategory_ShouldReturnResponse() {
        UUID id = UUID.randomUUID();
        CategoryRequest request = new CategoryRequest();
        CategoryResponse response = CategoryResponse.builder().build();
        ResponseEntity<DataResponse<CategoryResponse>> expected = ResponseEntity
            .status(HttpStatus.OK)
            .body(new DataResponse<>(HttpStatus.OK.value(), "success update category", response));
        
        when(categoryService.updateCategory(any(), any())).thenReturn(response);
        ResponseEntity<DataResponse<CategoryResponse>> actual = categoryController.updateCategory(id, request);
        assertEquals(expected, actual);
    }

    @Test
    void deleteCategory_ShouldReturnResponse() {
        UUID id = UUID.randomUUID();
        doNothing().when(categoryService).deleteCategory(id);
        ResponseEntity<HttpStatus> expected = ResponseEntity.noContent().build();  
        ResponseEntity<HttpStatus> actual = categoryController.deleteCategory(id);
        assertEquals(expected, actual);
    }
}
