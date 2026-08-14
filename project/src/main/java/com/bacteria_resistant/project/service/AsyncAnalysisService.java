package com.bacteria_resistant.project.service;

import com.bacteria_resistant.project.dto.PredictionResponse;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.Report;
import com.bacteria_resistant.project.model.enums.ReportStatus;
import com.bacteria_resistant.project.repository.ReportRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AsyncAnalysisService {

    private final PythonApiClientService apiService;
    private final ReportRepository reportRepository;

    public AsyncAnalysisService(PythonApiClientService apiService, ReportRepository reportRepository) {
        this.apiService = apiService;
        this.reportRepository = reportRepository;
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
            report.setStatus(ReportStatus.COMPLETED);

        } catch (Exception e) {
            System.err.println("Erro na integração com IA (Python) para o arquivo " + fileKey + ": " + e.getMessage());
            report.setStatus(ReportStatus.FAILED);
        }

        reportRepository.save(report);
    }


    public Report startAnalysis(Laboratory laboratory, MultipartFile file, String hospitalName, String hospitalUnit) {


        Report report = new Report();
        report.setLaboratory(laboratory);
        report.setHospitalName(hospitalName);
        report.setHospitalUnit(hospitalUnit);
        report.setStatus("PROCESSING");

        report = reportRepository.save(report);


        return report;
    }
}