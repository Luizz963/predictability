package com.bacteria_resistant.project.service.surveillance;

import com.bacteria_resistant.project.dto.BedStatusDTO;
import com.bacteria_resistant.project.dto.response.ClusterAlertDTO;
import com.bacteria_resistant.project.dto.response.DashboardStatsDTO;
import com.bacteria_resistant.project.dto.response.RecentSampleDTO;
import com.bacteria_resistant.project.mapper.ReportMapper;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.Report;
import com.bacteria_resistant.project.model.settings.InstitutionSettings;
import com.bacteria_resistant.project.repository.ReportRepository;
import com.bacteria_resistant.project.service.SettingsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final ReportRepository reportRepository;

    private final SettingsService settingsService;

    public DashboardService(ReportRepository reportRepository, SettingsService settingsService) {

        this.reportRepository = reportRepository;
        this.settingsService = settingsService;
    }

    public DashboardStatsDTO getConsolidatedStats(Laboratory laboratory) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);

        long totalSamples = reportRepository.countSamplesSince(laboratory, sevenDaysAgo);
        long resistantSamples = reportRepository.countResistantSamplesSince(laboratory, sevenDaysAgo);
        long monitoredUnits = reportRepository.countMonitoredUnitsSince(laboratory, sevenDaysAgo);

        InstitutionSettings settings = settingsService.getOrCreateEntity(laboratory);
        int minIsolates = settings.getMinIsolates();
        double threshold = settings.getPrevalenceThreshold() / 100.0;

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
            long resistantInUnit = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            if (totalInUnit >= minIsolates && ((double) resistantInUnit / totalInUnit) >= threshold) {
                activeClusters++;
                if (alert == null) {
                    String message = String.format("ALERTA CCIH: %d isolados com perfil genético de resistência identificados no setor %s nas últimas 48h. Risco de transmissão cruzada ativa.",
                            resistantInUnit, unit.toUpperCase());
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
                .map(ReportMapper::toRecentSampleDTO)
                .toList();
    }

    public List<BedStatusDTO> getBedsStatus(Laboratory laboratory) {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<Report> reports = reportRepository
                .findByLaboratoryAndProcessingDateAfterOrderByProcessingDateDesc(laboratory, since);

        Map<String, Report> latestByUnit = new LinkedHashMap<>();
        for (Report r : reports) {
            if (r.getHospitalUnit() != null) latestByUnit.putIfAbsent(r.getHospitalUnit(), r);
        }

        List<BedStatusDTO> beds = new ArrayList<>();
        for (Map.Entry<String, Report> e : latestByUnit.entrySet()) {
            Report r = e.getValue();
            boolean resistant = r.getResistanceSignal() != null && r.getResistanceSignal() == 1;
            beds.add(new BedStatusDTO(
                    e.getKey(),
                    extractBedLabel(e.getKey()),
                    resistant,
                    resistant,
                    r.getOrganism(),
                    r.getDetectedGene(),
                    r.getConfidenceLevel(),
                    r.getProcessingDate()
            ));
        }
        return beds;
    }

    private String extractBedLabel(String unit) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(?i)leito\\s*(\\d+)").matcher(unit);
        return m.find() ? m.group(1) : unit;
    }
}