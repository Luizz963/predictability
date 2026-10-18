package com.bacteria_resistant.project.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordDTO(
        @NotBlank(message = "Informe o CNPJ ou o e-mail cadastrado.")
        String identifier
) {}