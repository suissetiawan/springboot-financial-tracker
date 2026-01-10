package com.mini.project.financial_tracker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Double amount;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private String categoryName;
    private String categoryType;
    private UUID userId;
}
