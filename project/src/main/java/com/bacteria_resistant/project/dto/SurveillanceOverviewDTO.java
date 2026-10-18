package com.bacteria_resistant.project.dto;

import java.util.List;

public record SurveillanceOverviewDTO(
        List<WeekPointDTO> curve,
        List<AlertWindowDTO> windows,
        long alertWindowsCount,
        SurveillanceSummaryDTO summary
) {}