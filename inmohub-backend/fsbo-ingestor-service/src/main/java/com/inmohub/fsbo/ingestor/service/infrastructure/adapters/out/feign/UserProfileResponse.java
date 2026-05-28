package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.feign;

import java.util.UUID;

/**
 * DTO que transporta la respuesta del servicio de autenticacion con los datos de perfil del usuario.
 */
public record UserProfileResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone
) {
    public String getFullName() {
        return firstName + " " + lastName;
    }
}