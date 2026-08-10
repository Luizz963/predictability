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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Laboratory laboratory) {

        String filename = file.getOriginalFilename();

        if (filename == null || (!filename.endsWith(".fna") && !filename.endsWith(".fasta"))) {
            throw new InvalidFileException("Unsupported format. Please upload only .fna or .fasta files.");
        }

        try {
            // Gera um ID único para evitar sobreposição de arquivos com o mesmo nome
            String fileKey = UUID.randomUUID().toString() + "_" + filename;

            // Stream direto do MultipartFile (armazenado em disco temporário pelo Tomcat) para o S3
            storageService.uploadFile(
                    fileKey,
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType()
            );

            // Persiste o estado inicial com a chave do S3
            Report pendingReport = new Report(filename, fileKey, laboratory);
            Report savedReport = reportRepository.save(pendingReport);

            // Despacha a thread assíncrona passando apenas o ID e a Key
            asyncAnalysisService.processGenomeFile(savedReport.getId(), fileKey, filename);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(savedReport);

        } catch (IOException e) {
            throw new RuntimeException("Falha ao processar o fluxo do arquivo.", e);
        }
    }

    // GET listAll e GET findById permanecem inalterados.

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
}