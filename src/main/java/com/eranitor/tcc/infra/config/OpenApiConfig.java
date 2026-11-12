package com.eranitor.tcc.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Documentação OpenAPI/Swagger.
 *
 * UI: /swagger-ui.html | JSON: /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_BEARER = "bearerAuth";

    @Bean
    public OpenAPI eranitorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ERANITOR API")
                        .version("1.0.0")
                        .description("""
                                API REST do ERANITOR — sistema de gestão e priorização de estudos.

                                Autenticação: envie `Authorization: Bearer <token>` nas rotas protegidas.
                                O login (`POST /auth/login`) devolve `token` (2h) e `refreshToken` (30 dias).
                                """))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_BEARER))
                .components(new Components().addSecuritySchemes(
                        ESQUEMA_BEARER,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }
}
