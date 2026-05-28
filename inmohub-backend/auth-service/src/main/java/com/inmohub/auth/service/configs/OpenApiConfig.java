package com.inmohub.auth.service.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;

/**
 * Configuracion de OpenAPI/Swagger para el microservicio de autenticacion.
 * Define la informacion de la API, servidores y el esquema de seguridad JWT Bearer.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "InmoHub Auth Service API",
                version = "1.0.0",
                description = "Microservicio encargado de la gestión de usuarios, roles y autenticación.",
                contact = @Contact(name = "Joaquin Gabriel Puchuri Tunjar", email = "jvacotunjar@gmail.com")
        ),
        servers = {
                @Server(url = "/", description = "Servidor Local"),
                @Server(url = "http://localhost:8080", description = "API Gateway")
        }
)
@SecurityScheme(
        name = "Bearer",
        type = HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT token de acceso. Obtener mediante /login"
)
public class OpenApiConfig {

}