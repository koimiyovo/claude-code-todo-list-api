package com.kyovo.todo.adapter.input.web

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.Components
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val schemeName = "bearerAuth"
        return OpenAPI()
            .info(
                Info()
                    .title("Todo API")
                    .description("API REST de gestion de todos")
                    .version("1.0.0")
            )
            .addSecurityItem(SecurityRequirement().addList(schemeName))
            .components(
                Components().addSecuritySchemes(
                    schemeName,
                    SecurityScheme()
                        .name(schemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            )
    }
}
