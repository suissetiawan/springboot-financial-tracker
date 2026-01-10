package com.mini.project.financial_tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummaryResponse implements Serializable {
    private Double totalIncome;
    private Double totalExpense;
    private Double balance;
}
