package com.inmohub.auth.service.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Set;

/**
 * DTO para la creación de un nuevo usuario.
 */
public record UserCreateDto(
        @Schema(description = "Nombre de usuario único", example = "pepemontana")
        @NotBlank(message = "El username es obligatorio")
        String username,

        @Schema(description = "Contraseña del usuario (se almacenará hasheada)", example = "Segura123")
        @NotBlank(message = "La contraseña es obligatoria")
        String password,

        @Schema(description = "Correo electrónico corporativo o personal", example = "pepe.montana@gmail.com")
        @Email(message = "Formato de email inválido")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @Schema(description = "Nombre de pila", example = "Pepe")
        @NotBlank(message = "El nombre es obligatorio")
        String firstName,

        @Schema(description = "Apellidos", example = "Montana Botella")
        @NotBlank(message = "El apellido es obligatorio")
        String lastName,

        @Schema(description = "Teléfono móvil (9 dígitos, empieza por 6 o 7)", example = "600123456")
        @Pattern(regexp = "^[67]\\d{8}$", message = "Formato de teléfono inválido")
        String phone,

        Set<String> roles
//        @Schema(description = "Rol del usuario en la plataforma", example = "AGENT")
//        String role
) {
}
