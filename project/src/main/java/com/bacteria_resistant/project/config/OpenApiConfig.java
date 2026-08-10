package com.bacteria_resistant.project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Análise Genômica")
                        .version("1.0.0")
                        .description("Microsserviço de integração com IA para detecção de resistência bacteriana."));
    }
}
