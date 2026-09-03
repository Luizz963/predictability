package com.bacteria_resistant.project.controller;

import com.bacteria_resistant.project.dto.ClinicalFeedbackDTO;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final FeedbackService feedbackService;

    public ReportController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PutMapping("/{id}/feedback")
    public ResponseEntity<Void> submitFeedback(
            @PathVariable Long id,
            @RequestBody ClinicalFeedbackDTO feedback,
            @AuthenticationPrincipal Laboratory laboratory) {

        feedbackService.registerPhenotype(id, feedback.isResistant(), laboratory);
        return ResponseEntity.ok().build();
    }
}