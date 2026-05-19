package com.inmohub.auth.service.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Solicitud de actualización parcial del perfil de usuario")
public record UpdateUserProfileRequest(
        @Schema(description = "Nombre de pila", example = "Juan")
        String firstName,

        @Schema(description = "Apellidos", example = "Pérez")
        String lastName,

        @Schema(description = "Teléfono móvil (9 dígitos, empieza por 6 o 7)", example = "600123456")
        @Pattern(regexp = "^[67]\\d{8}$", message = "Formato de teléfono inválido")
        String phone
) {}
