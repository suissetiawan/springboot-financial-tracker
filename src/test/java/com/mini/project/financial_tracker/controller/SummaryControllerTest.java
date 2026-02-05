package com.mini.project.financial_tracker.controller;

import com.mini.project.financial_tracker.dto.response.DataResponse;
import com.mini.project.financial_tracker.dto.response.SummaryResponse;
import com.mini.project.financial_tracker.service.SummaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SummaryControllerTest {

    @Mock
    private SummaryService summaryService;

    @InjectMocks
    private SummaryController summaryController;

    @Test
    void getSummary_ShouldReturnResponse() {
        SummaryResponse response = new SummaryResponse(100.0, 50.0, 50.0);
        when(summaryService.getSummary()).thenReturn(response);
        
        ResponseEntity<DataResponse<SummaryResponse>> actual = summaryController.getSummary();
        assertEquals(200, actual.getStatusCode().value());
        assertEquals(response, actual.getBody().getResponse());
    }
}
