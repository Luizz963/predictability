package com.bacteria_resistant.project.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
        @NotBlank(message = "O CNPJ é obrigatório.")
        String taxId,

        @NotBlank(message = "A senha é obrigatória.")
        String password,

        @NotBlank(message = "O nome do laboratório é obrigatório.")
        String name
) {}