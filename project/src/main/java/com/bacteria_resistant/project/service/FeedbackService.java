package com.bacteria_resistant.project.service;

import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.Report;
import com.bacteria_resistant.project.repository.ReportRepository;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    private final ReportRepository reportRepository;

    public FeedbackService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public void registerPhenotype(Long reportId, boolean isResistant, Laboratory lab) {
        Report report = reportRepository.findByIdAndLaboratory(reportId, lab)
                .orElseThrow(() -> new RuntimeException("Laudo não encontrado ou acesso negado."));

        if (Boolean.TRUE.equals(report.getPhenotypeConfirmed())) {
            throw new RuntimeException("O gabarito deste laudo já foi confirmado anteriormente.");
        }

        report.setPhenotypeConfirmed(true);
        report.setActualResistance(isResistant ? 1 : 0);

        reportRepository.save(report);
    }
}