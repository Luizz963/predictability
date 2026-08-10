package com.bacteria_resistant.project.service;

import com.bacteria_resistant.project.dto.PredictionResponse;
import com.bacteria_resistant.project.model.Report;
import com.bacteria_resistant.project.model.enums.ReportStatus;
import com.bacteria_resistant.project.repository.ReportRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsyncAnalysisService {

    private final PythonApiClientService apiService;
    private final ReportRepository reportRepository;

    public AsyncAnalysisService(PythonApiClientService apiService, ReportRepository reportRepository) {
        this.apiService = apiService;
        this.reportRepository = reportRepository;
    }

    @Async("analysisExecutor")
    @Transactional
    public void processGenomeFile(Long reportId, String fileKey, String filename) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(ReportStatus.PROCESSING);
        reportRepository.saveAndFlush(report);

        try {

            PredictionResponse response = apiService.enviarParaIa(fileKey, filename);

            report.setAmpicillinResistance(response.ampicilinResistance());
            report.setProbability(response.probability());
            report.setStatus(ReportStatus.COMPLETED);

        } catch (Exception e) {
            report.setStatus(ReportStatus.FAILED);
        }

        reportRepository.save(report);
    }
}