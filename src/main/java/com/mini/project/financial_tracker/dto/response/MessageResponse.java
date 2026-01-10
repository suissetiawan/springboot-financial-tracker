package com.mini.project.financial_tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageResponse<T> {
    private Integer status;
    private T message;
}