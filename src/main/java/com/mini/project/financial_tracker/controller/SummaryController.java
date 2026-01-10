package com.mini.project.financial_tracker.controller;

import com.mini.project.financial_tracker.dto.response.DataResponse;
import com.mini.project.financial_tracker.dto.response.SummaryResponse;
import com.mini.project.financial_tracker.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping
    public ResponseEntity<DataResponse<SummaryResponse>> getSummary() {
        SummaryResponse response = summaryService.getSummary();
        return ResponseEntity.ok(new DataResponse<>(200, "Summary retrieved successfully", response));
    }
}
