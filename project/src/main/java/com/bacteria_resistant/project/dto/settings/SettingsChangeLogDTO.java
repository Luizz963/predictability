package com.bacteria_resistant.project.dto.settings;

import java.time.LocalDateTime;

public record SettingsChangeLogDTO(
        LocalDateTime changedAt,
        String changedBy,
        String description
) {}