package com.bacteria_resistant.project.dto;

import java.time.LocalDateTime;

public record BedStatusDTO(
        String unit,
        String bedLabel,
        boolean occupied,
        boolean isResistant,
        String organism,
        String gene,
        String confidence,
        LocalDateTime lastUpdate
) {}