package com.bacteria_resistant.project.dto.settings;


import java.util.List;

public record SettingsViewDTO(
        SettingsDTO settings,
        List<SettingsChangeLogDTO> history
) {}