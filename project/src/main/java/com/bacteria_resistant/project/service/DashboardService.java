package com.bacteria_resistant.project.service;

import com.bacteria_resistant.project.dto.ClusterAlertDTO;
import com.bacteria_resistant.project.dto.DashboardStatsDTO;
import com.bacteria_resistant.project.dto.RecentSampleDTO;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private final ReportRepository reportRepository;

    public DashboardService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public DashboardStatsDTO getConsolidatedStats(Laboratory laboratory) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);

        long totalSamples = reportRepository.countSamplesSince(laboratory, sevenDaysAgo);
        long resistantSamples = reportRepository.countResistantSamplesSince(laboratory, sevenDaysAgo);
        long monitoredUnits = reportRepository.countMonitoredUnitsSince(laboratory, sevenDaysAgo);

        String prevalence = "0%";
        if (totalSamples > 0) {
            long percentage = Math.round(((double) resistantSamples / totalSamples) * 100);
            prevalence = percentage + "%";
        }

        List<Object[]> clusterData = reportRepository.findClusterPotentials(laboratory, twoDaysAgo);
        long activeClusters = 0;
        ClusterAlertDTO alert = null;

        for (Object[] row : clusterData) {
            String unit = (String) row[0];
            long totalInUnit = ((Number) row[1]).longValue();

            // Proteção contra row[2] nulo para evitar erro no servidor
            long resistantInUnit = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            if (totalInUnit >= 3 && ((double) resistantInUnit / totalInUnit) >= 0.5) {
                activeClusters++;
                if (alert == null) {
                    String message = String.format("%d de %d amostras dos últimos 2 dias compartilham o mesmo determinante de resistência.",
                            resistantInUnit, totalInUnit);
                    alert = new ClusterAlertDTO(unit, message);
                }
            }
        }

        return new DashboardStatsDTO(totalSamples, prevalence, activeClusters, monitoredUnits, alert);
    }

    public List<RecentSampleDTO> getRecentSamples(Laboratory laboratory) {
        return reportRepository.findByLaboratory(laboratory,
                        org.springframework.data.domain.PageRequest.of(0, 10, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "processingDate")))
                .stream()
                .map(RecentSampleDTO::fromEntity)
                .toList();
    }
}