package com.bacteria_resistant.project.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(

        @NotBlank(message = "O taxId (CNPJ) é obrigatório.")
        String taxId,

        @NotBlank(message = "A senha é obrigatório.")
        String password

) {
}