package com.bacteria_resistant.project.dto;

import java.time.LocalDateTime;

public record WeekPointDTO(
        String label,
        LocalDateTime weekStart,
        LocalDateTime weekEnd,
        long samples,
        long resistant,
        double prevalenceRate
) {}