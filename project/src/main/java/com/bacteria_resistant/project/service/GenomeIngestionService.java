package com.bacteria_resistant.project.service;

import com.bacteria_resistant.project.exception.InvalidFileException;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.Report;
import com.bacteria_resistant.project.model.enums.ReportStatus;
import com.bacteria_resistant.project.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class GenomeIngestionService {

    private final StorageService storageService;
    private final ReportRepository reportRepository;
    private final AsyncAnalysisService asyncAnalysisService;

    public GenomeIngestionService(StorageService storageService,
                                  ReportRepository reportRepository,
                                  AsyncAnalysisService asyncAnalysisService) {
        this.storageService = storageService;
        this.reportRepository = reportRepository;
        this.asyncAnalysisService = asyncAnalysisService;
    }


    public Report ingestAndStartAnalysis(Laboratory laboratory, MultipartFile file, String hospitalName, String hospitalUnit) {
        String filename = file.getOriginalFilename();


        if (filename == null || (!filename.endsWith(".fna") && !filename.endsWith(".fasta"))) {
            throw new InvalidFileException("Unsupported format. Please upload only .fna or .fasta files.");
        }

        try {

            String safeHospitalName = hospitalName.replaceAll("\\s+","_").toLowerCase();
            String fileKey = laboratory.getId() + "/" + safeHospitalName + "/" + UUID.randomUUID().toString() + "_" + filename;

            storageService.uploadFile(fileKey, file.getInputStream(), file.getSize(), file.getContentType());

            Report report = new Report(filename, fileKey, laboratory);
            report.setHospitalName(hospitalName);
            report.setHospitalUnit(hospitalUnit);
            report.setStatus(ReportStatus.PROCESSING);
            Report savedReport = reportRepository.saveAndFlush(report);

            asyncAnalysisService.processGenomeFile(savedReport.getId(), fileKey, filename);

            return savedReport;

        } catch (IOException e) {
            throw new RuntimeException("Falha ao processar o fluxo de I/O do arquivo.", e);
        }
    }
}