package com.bacteria_resistant.project.dto.request;

import com.bacteria_resistant.project.validation.Cnpj;
import com.bacteria_resistant.project.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
        @NotBlank(message = "O CNPJ é obrigatório.")
        @Cnpj
        String taxId,

        @NotBlank(message = "A senha é obrigatória.")
        @StrongPassword
        String password,

        @NotBlank(message = "O nome do laboratório é obrigatório.")
        String name,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "E-mail inválido: verifique o formato.")
        String email
) {}