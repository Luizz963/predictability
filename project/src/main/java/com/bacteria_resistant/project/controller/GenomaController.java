package com.bacteria_resistant.project.controller;

import com.bacteria_resistant.project.exception.InvalidFileException;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.Report;
import com.bacteria_resistant.project.repository.ReportRepository;
import com.bacteria_resistant.project.service.AsyncAnalysisService;
import com.bacteria_resistant.project.service.StorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/genomes")
public class GenomaController {

    private final ReportRepository reportRepository;
    private final AsyncAnalysisService asyncAnalysisService;
    private final StorageService storageService;

    public GenomaController(ReportRepository reportRepository,
                            AsyncAnalysisService asyncAnalysisService,
                            StorageService storageService) {
        this.reportRepository = reportRepository;
        this.asyncAnalysisService = asyncAnalysisService;
        this.storageService = storageService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<Report> analyzeGenome(
            @AuthenticationPrincipal Laboratory laboratory,
            @RequestParam("file") MultipartFile file,
            @RequestParam("hospitalName") String hospitalName,
            @RequestParam("hospitalUnit") String hospitalUnit)
    {

        String filename = file.getOriginalFilename();

        if (filename == null || (!filename.endsWith(".fna") && !filename.endsWith(".fasta"))) {
            throw new InvalidFileException("Unsupported format. Please upload only .fna or .fasta files.");
        }
        try {
            String fileKey = UUID.randomUUID().toString() + "_" + filename;
            storageService.uploadFile(
                    fileKey,
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType()
            );


            Report pendingReport = new Report(filename, fileKey, laboratory);
            Report savedReport = reportRepository.save(pendingReport);

            asyncAnalysisService.processGenomeFile(savedReport.getId(), fileKey, filename);


            Report report = asyncAnalysisService.startAnalysis(laboratory, file, hospitalName, hospitalUnit);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(savedReport);

        } catch (IOException e) {
            throw new RuntimeException("Falha ao processar o fluxo do arquivo.", e);
        }
    }



    @GetMapping
    public ResponseEntity<Page<Report>> listAll(
            @AuthenticationPrincipal Laboratory laboratory,
            @PageableDefault(size = 10, sort = "processingDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Report> reports = reportRepository.findByLaboratory(laboratory, pageable);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal Laboratory laboratory) {

        return reportRepository.findById(id)
                .filter(report -> report.getLaboratory().getId().equals(laboratory.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/surveillance")
    public ResponseEntity<Page<Report>> getOutbreakAlerts(
            @AuthenticationPrincipal Laboratory laboratory,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0.90") Double minProbability,
            @PageableDefault(size = 20, sort = "processingDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Report> outbreakReports = reportRepository.findOutbreakPatterns(
                laboratory, startDate, endDate, minProbability, pageable);

        return ResponseEntity.ok(outbreakReports);
    }
}