
package com.bacteria_resistant.project.service.surveillance;

import com.bacteria_resistant.project.dto.AlertWindowDTO;
import com.bacteria_resistant.project.dto.SurveillanceOverviewDTO;
import com.bacteria_resistant.project.dto.SurveillanceSummaryDTO;
import com.bacteria_resistant.project.dto.WeekPointDTO;
import com.bacteria_resistant.project.model.settings.InstitutionSettings;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.Report;
import com.bacteria_resistant.project.repository.ReportRepository;
import com.bacteria_resistant.project.service.SettingsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;

@Service
public class SurveillanceOverviewService {

    private final ReportRepository reportRepository;
    private final SettingsService settingsService;

    public SurveillanceOverviewService(ReportRepository reportRepository, SettingsService settingsService) {
        this.reportRepository = reportRepository;
        this.settingsService = settingsService;
    }

    public SurveillanceOverviewDTO buildOverview(Laboratory lab, String unit, int weeks) {
        InstitutionSettings settings = settingsService.getOrCreateEntity(lab);
        double prevalenceThreshold = settings.getPrevalenceThreshold() / 100.0;
        int minIsolates = settings.getMinIsolates();
        double minConfidence = settings.getMinConfidence() / 100.0;

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusWeeks(weeks);

        List<Report> reports = reportRepository
                .findByLaboratoryAndProcessingDateAfterOrderByProcessingDateDesc(lab, start);

        if (unit != null && !unit.isBlank()) {
            reports = reports.stream().filter(r -> unit.equals(r.getHospitalUnit())).toList();
        }

        // Monta a curva semana a semana (da mais antiga para a atual)
        List<WeekPointDTO> curve = new ArrayList<>();
        for (int i = weeks; i >= 1; i--) {
            LocalDateTime ws = end.minusWeeks(i);
            LocalDateTime we = end.minusWeeks(i - 1);
            List<Report> inWindow = reports.stream()
                    .filter(r -> !r.getProcessingDate().isBefore(ws) && r.getProcessingDate().isBefore(we))
                    .toList();
            long samples = inWindow.size();
            long resistant = countResistant(inWindow);
            double prevalence = samples == 0 ? 0.0 : (double) resistant / samples;
            curve.add(new WeekPointDTO(weekLabel(we), ws, we, samples, resistant, prevalence));
        }

        // Janelas com status aplicado pelas regras configuradas
        List<AlertWindowDTO> windows = curve.stream()
                .map(w -> new AlertWindowDTO(
                        w.label(), w.weekStart(), w.weekEnd(),
                        w.samples(), w.resistant(), w.prevalenceRate(),
                        w.samples() >= minIsolates && w.prevalenceRate() >= prevalenceThreshold))
                .toList();

        long alertCount = windows.stream().filter(AlertWindowDTO::alertTriggered).count();

        long total = reports.size();
        long resistantTotal = countResistant(reports);
        long highConf = reports.stream()
                .filter(r -> r.getProbability() != null
                        && Math.max(r.getProbability(), 1.0 - r.getProbability()) >= minConfidence)
                .count();
        double currentPrevalence = total == 0 ? 0.0 : (double) resistantTotal / total;
        boolean outbreak = alertCount > 0;

        SurveillanceSummaryDTO summary = new SurveillanceSummaryDTO(
                total, highConf, currentPrevalence, outbreak, outbreak ? "Surto ativo" : "Controlado");

        return new SurveillanceOverviewDTO(curve, windows, alertCount, summary);
    }

    private long countResistant(List<Report> reports) {
        return reports.stream()
                .filter(r -> r.getResistanceSignal() != null && r.getResistanceSignal() == 1)
                .count();
    }

    private String weekLabel(LocalDateTime date) {
        return "Sem " + date.get(WeekFields.ISO.weekOfWeekBasedYear());
    }
}