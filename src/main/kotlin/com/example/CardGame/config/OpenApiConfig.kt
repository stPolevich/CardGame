package com.example.CardGame.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val securitySchemeName = "Bearer Authentication"

        return OpenAPI()
            .info(
                Info()
                    .title("Card Game API")
                    .description("""
    # Аутентификация
                        
    Для доступа к защищенным endpoint'ам необходимо:
                        
        1. Получить JWT токен через POST `/api/auth/register`
                        
        2. Нажать кнопку 'Authorize'
                        
        3. Ввести токен в формате: `Bearer ваш_токен`
                    """)
                    .version("1.0")
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description(
                                """
        Для доступа к защищенным эндпоинтам используйте JWT токен.
            1. Получите токен через /api/auth/register
            2. Введите токен в формате: Bearer <ваш_токен>
                                """
                            )
                    )
            )
            .addSecurityItem(SecurityRequirement().addList(securitySchemeName))
    }
}