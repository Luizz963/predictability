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


    private static final int SURTO_WINDOW_DAYS = 7;
    private static final int MIN_SAMPLES_FOR_CLUSTER = 3;
    private static final double CONFIDENCE_THRESHOLD = 0.85;

    public SurveillanceAggregationService(ReportRepository reportRepository, SurveillanceAlertRepository alertRepository) {
        this.reportRepository = reportRepository;
        this.alertRepository = alertRepository;
    }

    @Transactional
    public void aggregateAndPersist(Laboratory lab, String hospitalName, String hospitalUnit) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(SURTO_WINDOW_DAYS);


        long totalSamples = reportRepository.countByLaboratoryAndHospitalUnitInWindow(lab, hospitalUnit, start, end);
        if (totalSamples == 0) return;


        long highConfidenceCount = reportRepository.countHighConfidenceInWindow(lab, hospitalUnit, start, end, CONFIDENCE_THRESHOLD);

        double prevalence = (double) highConfidenceCount / totalSamples;


        boolean triggered = (highConfidenceCount >= MIN_SAMPLES_FOR_CLUSTER) && (prevalence >= 0.5);


        if (triggered) {
            SurveillanceAlert alert = new SurveillanceAlert();
            alert.setLaboratory(lab);
            alert.setHospitalName(hospitalName);
            alert.setHospitalUnit(hospitalUnit);
            alert.setWindowStart(start);
            alert.setWindowEnd(end);
            alert.setSampleCount((int) totalSamples);
            alert.setHighConfidenceCount((int) highConfidenceCount);
            alert.setPrevalenceRate(prevalence);
            alert.setAlertTriggered(true);
            alert.setGeneratedAt(end);

            alertRepository.save(alert);
        }
    }
}