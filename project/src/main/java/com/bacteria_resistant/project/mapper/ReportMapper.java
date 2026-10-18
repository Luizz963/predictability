package com.bacteria_resistant.project.mapper;

import com.bacteria_resistant.project.dto.response.RecentSampleDTO;
import com.bacteria_resistant.project.model.Report;
import java.time.format.DateTimeFormatter;

public class ReportMapper {

    public static RecentSampleDTO toRecentSampleDTO(Report report) {
        String formattedDate = report.getProcessingDate() != null
                ? report.getProcessingDate().format(DateTimeFormatter.ofPattern("dd/MM"))
                : "--/--";

        String confidence = "Baixa";
        boolean isResistant = false;

        if (report.getResistanceSignal() != null && report.getResistanceSignal() == 1) {
            confidence = "Alta";
            isResistant = true;
        } else if (report.getProbability() != null) {
            double prob = report.getProbability();
            double certeza = Math.max(prob, 1.0 - prob);

            if (certeza >= 0.85) {
                confidence = "Alta";
            } else if (certeza >= 0.65) {
                confidence = "Média";
            } else {
                confidence = "Baixa";
            }
        }

        String gene = (report.getDetectedGene() != null && !report.getDetectedGene().equalsIgnoreCase("Nenhum"))
                ? report.getDetectedGene()
                : "não detectado";

        return new RecentSampleDTO(
                report.getId(),
                formattedDate,
                report.getHospitalUnit(),
                gene,
                confidence,
                isResistant,
                report.getStatus().name()
        );
    }
}