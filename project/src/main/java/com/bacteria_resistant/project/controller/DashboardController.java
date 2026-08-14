package com.bacteria_resistant.project.controller;

import com.bacteria_resistant.project.dto.DashboardStatsDTO;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(@AuthenticationPrincipal Laboratory laboratory) {
        DashboardStatsDTO stats = dashboardService.getConsolidatedStats(laboratory);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent")
    public ResponseEntity<java.util.List<com.bacteria_resistant.project.dto.RecentSampleDTO>> getRecentSamples(
            @AuthenticationPrincipal Laboratory laboratory) {
        return ResponseEntity.ok(dashboardService.getRecentSamples(laboratory));
    }
}