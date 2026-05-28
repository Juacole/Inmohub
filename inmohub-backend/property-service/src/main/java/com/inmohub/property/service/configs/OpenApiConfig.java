package com.inmohub.property.service.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "InmoHub Property Service API",
                version = "1.0.0",
                description = "Microservicio encargado de la gestión de propiedades inmobiliarias.",
                contact = @Contact(name = "Joaquin Gabriel Puchuri Tunjar", email = "jvacotunjar@gmail.com")
        ),
        servers = {
                @Server(url = "/", description = "Servidor Local"),
                @Server(url = "http://localhost:8080", description = "API Gateway")
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Bearer Token de autenticación. Obtener token mediante `/api/v1/auth/login`",
        type = HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
/**
 * Configuracion de la documentacion OpenAPI/Swagger para el Property Service.
 * Define los metadatos de la API, servidores disponibles y esquema de seguridad JWT Bearer.
 */
public class OpenApiConfig {

}