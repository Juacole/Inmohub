package com.inmohub.auth.service.dtos;

public record AuthResponseDto(
        String accessToken,
        String refreshToken
) {}