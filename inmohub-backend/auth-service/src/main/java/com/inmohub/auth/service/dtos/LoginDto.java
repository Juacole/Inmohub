package com.inmohub.auth.service.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO que transporta las credenciales de inicio de sesion (email y contrasena).
 */
@Schema(description = "Objeto de transferencia para las credenciales de inicio de sesión")
public record LoginDto(
        @Schema(description = "Email del usuario", example = "pepe.montana@gmail.com")
        String email,

        @Schema(description = "Contraseña en texto plano", example = "Password123")
        String password
) {
}
