package com.bacteria_resistant.project.dto.response;

public record RecentSampleDTO(
        Long id,
        String date,
        String unit,
        String detectedGene,
        String confidence,
        boolean isResistant,
        String status
) {}