package com.bacteria_resistant.project.dto;

public record DashboardStatsDTO(
        long totalSamples7Days,
        String ampicillinPrevalence,
        long activeClusters,
        long monitoredUnits,
        ClusterAlertDTO activeAlert
) {}