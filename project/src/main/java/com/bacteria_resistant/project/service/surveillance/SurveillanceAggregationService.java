package com.bacteria_resistant.project.service.surveillance;

import com.bacteria_resistant.project.model.settings.InstitutionSettings;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.SurveillanceAlert;
import com.bacteria_resistant.project.repository.ReportRepository;
import com.bacteria_resistant.project.repository.SurveillanceAlertRepository;
import com.bacteria_resistant.project.service.SettingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SurveillanceAggregationService {

    private final ReportRepository reportRepository;
    private final SurveillanceAlertRepository alertRepository;
    private final SettingsService settingsService;   // <-- CAMPO QUE FALTAVA

    public SurveillanceAggregationService(ReportRepository reportRepository,
                                          SurveillanceAlertRepository alertRepository,
                                          SettingsService settingsService) {   // <-- PARÂMETRO NOVO
        this.reportRepository = reportRepository;
        this.alertRepository = alertRepository;
        this.settingsService = settingsService;      // <-- ATRIBUIÇÃO NOVA
    }

    @Transactional
    public void aggregateAndPersist(Laboratory lab, String hospitalName, String hospitalUnit) {
        InstitutionSettings settings = settingsService.getOrCreateEntity(lab);

        int windowDays = settings.getWindowDays();
        int minSamples = settings.getMinIsolates();
        double confidenceThreshold = settings.getMinConfidence() / 100.0;
        double prevalenceThreshold = settings.getPrevalenceThreshold() / 100.0;

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(windowDays);

        long totalSamples = reportRepository.countByLaboratoryAndHospitalUnitInWindow(lab, hospitalUnit, start, end);
        if (totalSamples == 0) return;

        long highConfidenceCount = reportRepository.countHighConfidenceInWindow(
                lab, hospitalUnit, start, end, confidenceThreshold);

        double prevalence = (double) highConfidenceCount / totalSamples;

        boolean triggered = (highConfidenceCount >= minSamples) && (prevalence >= prevalenceThreshold);

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