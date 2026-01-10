package com.mini.project.financial_tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataResponse<T> {
    private Integer status;
    private String message;
    private T response;
}
