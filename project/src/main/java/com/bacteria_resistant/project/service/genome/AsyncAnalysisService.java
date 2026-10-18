package com.bacteria_resistant.project.service.genome;

import com.bacteria_resistant.project.model.Report;
import com.bacteria_resistant.project.model.enums.ReportStatus;
import com.bacteria_resistant.project.repository.ReportRepository;

import com.bacteria_resistant.project.dto.response.PredictionResponseDTO;


import com.bacteria_resistant.project.service.surveillance.SurveillanceAggregationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncAnalysisService {

    private final MotorGenomicoService motorGenomicoService;
    private final ReportRepository reportRepository;
    private final SurveillanceAggregationService surveillanceAggregationService;

    public AsyncAnalysisService(MotorGenomicoService motorGenomicoService, ReportRepository reportRepository, SurveillanceAggregationService surveillanceAggregationService) {
        this.motorGenomicoService = motorGenomicoService;
        this.reportRepository = reportRepository;
        this.surveillanceAggregationService = surveillanceAggregationService;
    }

    @Async("analysisExecutor")
    public void processGenomeFile(Long reportId, String fileKey) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(ReportStatus.PROCESSING);
        reportRepository.saveAndFlush(report);

        try {

            PredictionResponseDTO prediction = motorGenomicoService.analyzeGenome(fileKey);


            report.setResistanceSignal(prediction.mlSignal());
            report.setProbability(prediction.mlProbability());

            if (prediction.detectedGene() != null && !prediction.detectedGene().isBlank()) {
                report.setDetectedGene(prediction.detectedGene());
            } else {
                report.setDetectedGene("Nenhum");
            }


            report.setConfidenceLevel(prediction.confidenceLevel());

            report.setStatus(ReportStatus.COMPLETED);
            reportRepository.save(report);


            surveillanceAggregationService.aggregateAndPersist(
                    report.getLaboratory(), report.getHospitalName(), report.getHospitalUnit()
            );

        } catch (Exception e) {
            System.err.println("Erro na execução da API de inferência: " + e.getMessage());
            report.setStatus(ReportStatus.FAILED);
            reportRepository.save(report);
        }
    }
}