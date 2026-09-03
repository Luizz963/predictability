package com.bacteria_resistant.project.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PredictionResponse(

        @JsonProperty("mlSignal")
        int ampicilinResistance,

        @JsonProperty("mlProbability")
        double probability,

        @JsonProperty("geneDetected")
        boolean geneDetected,

        @JsonProperty("detectedGene")
        String detectedGene,

        @JsonProperty("resistanceClass")
        String resistanceClass,

        @JsonProperty("confidenceLevel")
        String confidenceLevel
) {
}