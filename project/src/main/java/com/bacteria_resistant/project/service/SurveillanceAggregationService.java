package com.bacteria_resistant.project.service;

import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.SurveillanceAlert;
import com.bacteria_resistant.project.repository.ReportRepository;
import com.bacteria_resistant.project.repository.SurveillanceAlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class SurveillanceAggregationService {

    private final ReportRepository reportRepository;
    private final SurveillanceAlertRepository alertRepository;

    public SurveillanceAggregationService(ReportRepository reportRepository, SurveillanceAlertRepository alertRepository) {
        this.reportRepository = reportRepository;
        this.alertRepository = alertRepository;
    }

    @Transactional
    public void aggregateAndPersist(Laboratory lab, String hospitalName, String hospitalUnit) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(7);

        long totalSamples = reportRepository.countByLaboratoryAndHospitalUnitInWindow(lab, hospitalUnit, start, end);
        long resistantSamples = reportRepository.countResistantByLaboratoryAndHospitalUnitInWindow(lab, hospitalUnit, start, end);

        if (totalSamples == 0) return;

        double prevalence = (double) resistantSamples / totalSamples;
        boolean triggered = (totalSamples >= 3 && prevalence >= 0.50);

        SurveillanceAlert alert = new SurveillanceAlert();
        alert.setLaboratory(lab);
        alert.setHospitalName(hospitalName);
        alert.setHospitalUnit(hospitalUnit);
        alert.setWindowStart(start);
        alert.setWindowEnd(end);
        alert.setSampleCount((int) totalSamples);
        alert.setHighConfidenceCount((int) resistantSamples);
        alert.setPrevalenceRate(prevalence);
        alert.setAlertTriggered(triggered);
        alert.setGeneratedAt(end);

        alertRepository.save(alert);
    }
}