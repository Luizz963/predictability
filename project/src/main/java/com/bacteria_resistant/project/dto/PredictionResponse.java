package com.bacteria_resistant.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PredictionResponse(

        @JsonProperty("resistencia_ampicilina")
        int ampicilinResistance,

        @JsonProperty("probabilidade")
        double probability
) {
}
