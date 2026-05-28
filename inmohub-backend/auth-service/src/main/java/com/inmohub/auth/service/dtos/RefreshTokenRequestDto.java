package com.inmohub.auth.service.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO que transporta el refresh token necesario para solicitar un nuevo token de acceso.
 */
public record RefreshTokenRequestDto(
        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken
) {}