package com.bacteria_resistant.project.dto.response;

public record PredictionResponseDTO(
         Integer mlSignal,
         Double mlProbability,
         Boolean geneDetected,
         String detectedGene,
         String resistanceClass,
         String confidenceLevel
) {
}
