package com.inmohub.fsbo.ingestor.service.domain.models;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.DomainException;

import java.util.UUID;

/**
 * Datos del propietario de un archivo FSBO: identificador, nombre, email y telefono.
 */
public record OwnerDetails(
        UUID ownerId,
        String fullName,
        String email,
        String phone
) {
    public OwnerDetails {
        if (ownerId == null) throw new DomainException("El ID del propietario no puede ser nulo.");
        if (fullName == null || fullName.isBlank()) throw new DomainException("El nombre es obligatorio.");
        if (email == null || email.isBlank()) throw new DomainException("El email es obligatorio.");
        if (phone == null || phone.isBlank()) throw new DomainException("El numero telefonico es obligatorio.");
    }
}