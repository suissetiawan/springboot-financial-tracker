package com.mini.project.financial_tracker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@JsonPropertyOrder({
        "id",
        "amount",
        "description",
        "updatedAt",
        "categoryName",
        "categoryType",
        "userId"
})
public class TransactionDetailResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Double amount;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private String categoryName;
    private String categoryType;
    private UUID userId;
}
