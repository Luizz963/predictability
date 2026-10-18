package com.bacteria_resistant.project.dto;

public record SurveillanceSummaryDTO(
        long totalSamples,
        long highConfidenceSamples,
        double currentPrevalence,
        boolean outbreakActive,
        String situation
) {}