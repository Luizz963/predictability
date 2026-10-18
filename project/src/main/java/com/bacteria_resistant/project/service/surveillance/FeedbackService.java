package com.bacteria_resistant.project.service.surveillance;

import com.bacteria_resistant.project.dto.response.RecentSampleDTO;
import com.bacteria_resistant.project.exception.DuplicateFeedbackException;
import com.bacteria_resistant.project.exception.ResourceNotFoundException;
import com.bacteria_resistant.project.mapper.ReportMapper;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.Report;
import com.bacteria_resistant.project.model.enums.ReportStatus;
import com.bacteria_resistant.project.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    private final ReportRepository reportRepository;

    public FeedbackService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public void registerPhenotype(Long reportId, boolean isResistant, Laboratory lab) {
        Report report = reportRepository.findByIdAndLaboratory(reportId, lab)
                .orElseThrow(() -> new ResourceNotFoundException("Laudo não encontrado ou acesso negado."));

        if (Boolean.TRUE.equals(report.getPhenotypeConfirmed())) {
            throw new DuplicateFeedbackException("O gabarito deste laudo já foi confirmado anteriormente.");
        }

        report.setPhenotypeConfirmed(true);
        report.setActualResistance(isResistant ? 1 : 0);
        reportRepository.save(report);
    }

    public List<RecentSampleDTO> getPending(Laboratory lab) {
        return reportRepository
                .findByLaboratoryAndStatusAndPhenotypeConfirmedFalseOrderByProcessingDateDesc(
                        lab, ReportStatus.COMPLETED)
                .stream()
                .map(ReportMapper::toRecentSampleDTO)
                .toList();
    }
}