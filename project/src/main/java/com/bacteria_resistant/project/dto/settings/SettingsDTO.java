package com.bacteria_resistant.project.dto.settings;

public record SettingsDTO(
        Integer windowDays,
        Integer minIsolates,
        Integer prevalenceThreshold,
        Integer minConfidence,
        String institutionName,
        String taxId,
        String technicalResponsible,
        String ccihEmail
) {}