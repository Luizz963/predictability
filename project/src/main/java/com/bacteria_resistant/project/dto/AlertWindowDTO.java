package com.bacteria_resistant.project.dto;

import java.time.LocalDateTime;

public record AlertWindowDTO(
        String label,
        LocalDateTime windowStart,
        LocalDateTime windowEnd,
        long samples,
        long resistant,
        double prevalenceRate,
        boolean alertTriggered
) {}