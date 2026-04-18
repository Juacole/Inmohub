package com.inmohub.property.service.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "InmoHub Property Service API",
                version = "1.0.0",
                description = "Microservicio encargado de la gestión de propiedades.",
                contact = @Contact(name = "Joaquin Gabriel Puchuri Tunjar", email = "jvacotunjar@gmail.com")
        ),
        servers = {
                @Server(url = "/", description = "Servidor Local"),
                @Server(url = "http://localhost:8080", description = "API Gateway")
        }
)
public class OpenApiConfig {

}