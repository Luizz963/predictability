package com.bacteria_resistant.project.dto.response;

public record DashboardStatsDTO(
        long totalSamples7Days,
        String resistancePrevalence,
        long activeClusters,
        long monitoredUnits,
        ClusterAlertDTO activeAlert
) {}