package com.inmohub.property.service.dtos;

import java.util.UUID;

/**
 * DTO interno que representa la respuesta simplificada que recibimos desde Auth-Service.
 * Solo necesitamos estos campos para validar la lógica de negocio (existencia y estado).
 */
public record UserResponseDto(
        UUID id,
        String email,
        String role,
        String status
) {
}
