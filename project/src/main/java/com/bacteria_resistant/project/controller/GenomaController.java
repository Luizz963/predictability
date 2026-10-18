package com.bacteria_resistant.project.controller;

import com.bacteria_resistant.project.dto.response.SurveillanceAlertDTO;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.repository.SurveillanceAlertRepository;
import com.bacteria_resistant.project.service.genome.GenomeIngestionService;
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
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/genomes")
public class GenomaController {

    private final GenomeIngestionService ingestionService;
    private final SurveillanceAlertRepository surveillanceAlertRepository;

    public GenomaController(GenomeIngestionService ingestionService, SurveillanceAlertRepository surveillanceAlertRepository) {
        this.ingestionService = ingestionService;
        this.surveillanceAlertRepository = surveillanceAlertRepository;
    }


    @PostMapping("/analyze")
    public ResponseEntity<Void> analyzeGenome(
            @AuthenticationPrincipal Laboratory laboratory,
            @RequestParam("file") List<MultipartFile> files,
            @RequestParam("hospitalName") String hospitalName,
            @RequestParam("hospitalUnit") String hospitalUnit,
            @RequestParam(value = "organism", required = false) String organism) {

        for (MultipartFile file : files) {
            ingestionService.ingestAndStartAnalysis(laboratory, file, hospitalName, hospitalUnit, organism);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }


    @GetMapping("/surveillance")
    public ResponseEntity<Page<SurveillanceAlertDTO>> getOutbreakAlerts(
            @AuthenticationPrincipal Laboratory laboratory,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "generatedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<SurveillanceAlertDTO> alerts = surveillanceAlertRepository
                .findByLaboratoryAndGeneratedAtBetween(laboratory, startDate, endDate, pageable)
                .map(SurveillanceAlertDTO::fromEntity);

        return ResponseEntity.ok(alerts);
    }
}