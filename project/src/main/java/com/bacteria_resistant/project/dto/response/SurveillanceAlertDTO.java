package com.bacteria_resistant.project.dto.response;

import com.bacteria_resistant.project.model.SurveillanceAlert;
import java.time.LocalDateTime;

public record SurveillanceAlertDTO(
        String hospitalName,
        String hospitalUnit,
        LocalDateTime windowStart,
        LocalDateTime windowEnd,
        Integer sampleCount,
        Integer highConfidenceCount,
        Double prevalenceRate,
        Boolean alertTriggered,
        LocalDateTime generatedAt
) {
    public static SurveillanceAlertDTO fromEntity(SurveillanceAlert alert) {
        return new SurveillanceAlertDTO(
                alert.getHospitalName(),
                alert.getHospitalUnit(),
                alert.getWindowStart(),
                alert.getWindowEnd(),
                alert.getSampleCount(),
                alert.getHighConfidenceCount(),
                alert.getPrevalenceRate(),
                alert.getAlertTriggered(),
                alert.getGeneratedAt()
        );
    }
}