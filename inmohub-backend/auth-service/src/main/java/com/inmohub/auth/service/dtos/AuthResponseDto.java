package com.inmohub.auth.service.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO que transporta la respuesta de autenticacion con el token de acceso JWT y el refresh token.
 */
public record AuthResponseDto(
        @NotBlank(message = "El token es obligatorio")
        String accessToken,
        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken
) {}