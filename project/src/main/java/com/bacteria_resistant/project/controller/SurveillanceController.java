package com.bacteria_resistant.project.controller;

import com.bacteria_resistant.project.dto.SurveillanceOverviewDTO;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.service.surveillance.SurveillanceOverviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/surveillance")
public class SurveillanceController {

    private final SurveillanceOverviewService overviewService;

    public SurveillanceController(SurveillanceOverviewService overviewService) {
        this.overviewService = overviewService;
    }

    @GetMapping("/overview")
    public ResponseEntity<SurveillanceOverviewDTO> overview(
            @AuthenticationPrincipal Laboratory laboratory,
            @RequestParam(required = false) String unit,
            @RequestParam(defaultValue = "8") int weeks) {
        return ResponseEntity.ok(overviewService.buildOverview(laboratory, unit, weeks));
    }
}