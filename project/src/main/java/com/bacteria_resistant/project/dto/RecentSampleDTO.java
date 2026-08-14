package com.bacteria_resistant.project.dto;

import java.time.format.DateTimeFormatter;

public record RecentSampleDTO(
        String data,
        String unidade,
        String geneDetectado,
        String confianca
) {
    public static RecentSampleDTO fromEntity(com.bacteria_resistant.project.model.Report report) {

        String formattedDate = report.getProcessingDate() != null
                ? report.getProcessingDate().format(DateTimeFormatter.ofPattern("dd/MM"))
                : "--/--";


        String confidence = "Baixa";
        if (report.getProbability() != null) {
            if (report.getProbability() >= 0.85 && report.getAmpicillinResistance() == 1) {
                confidence = "Alta";
            } else if (report.getProbability() >= 0.50) {
                confidence = "Investigar";
            }
        }

        String gene = report.getDetectedGene() != null ? report.getDetectedGene() : "não detectado";

        return new RecentSampleDTO(formattedDate, report.getHospitalUnit(), gene, confidence);
    }
}