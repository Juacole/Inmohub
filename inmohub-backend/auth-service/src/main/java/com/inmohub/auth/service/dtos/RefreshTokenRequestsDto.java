package com.inmohub.auth.service.dtos;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestsDto(
        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken
) {}