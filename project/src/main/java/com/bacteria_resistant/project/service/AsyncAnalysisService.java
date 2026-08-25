package com.bacteria_resistant.project.service;

import com.bacteria_resistant.project.dto.response.PredictionResponse;
import com.bacteria_resistant.project.model.Report;
import com.bacteria_resistant.project.model.enums.ReportStatus;
import com.bacteria_resistant.project.repository.ReportRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncAnalysisService {

    private final PythonApiClientService apiService;
    private final ReportRepository reportRepository;
    private final SurveillanceAggregationService surveillanceAggregationService;

    public AsyncAnalysisService(PythonApiClientService apiService, ReportRepository reportRepository, SurveillanceAggregationService surveillanceAggregationService) {
        this.apiService = apiService;
        this.reportRepository = reportRepository;
        this.surveillanceAggregationService = surveillanceAggregationService;
    }

    @Async("analysisExecutor")
    public void processGenomeFile(Long reportId, String fileKey, String filename) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(ReportStatus.PROCESSING);
        reportRepository.saveAndFlush(report);

        try {
            PredictionResponse response = apiService.enviarParaIa(fileKey, filename);

            report.setAmpicillinResistance(response.ampicilinResistance());
            report.setProbability(response.probability());
            report.setDetectedGene(response.detectedGene());
            report.setStatus(ReportStatus.COMPLETED);
            reportRepository.save(report);


            surveillanceAggregationService.aggregateAndPersist(
                    report.getLaboratory(), report.getHospitalName(), report.getHospitalUnit()
            );

        } catch (Exception e) {
            System.err.println("Erro na integração com IA (Python): " + e.getMessage());
            report.setStatus(ReportStatus.FAILED);
            reportRepository.save(report);
        }
    }
}