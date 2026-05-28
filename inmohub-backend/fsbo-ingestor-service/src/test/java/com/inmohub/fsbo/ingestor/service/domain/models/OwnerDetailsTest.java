package com.inmohub.fsbo.ingestor.service.domain.models;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para {@link OwnerDetails}.
 * Verifica que los campos obligatorios del propietario (id, nombre, email, telefono)
 * no puedan ser nulos ni vacios al construir el record.
 */
@DisplayName("OwnerDetails (Modelo de Dominio)")
class OwnerDetailsTest {

    private final UUID ownerId = UUID.randomUUID();

    @Test
    @DisplayName("Debe crear OwnerDetails con datos validos")
    void crearOwnerValido() {
        OwnerDetails owner = new OwnerDetails(ownerId, "Pepe Montana", "pepe@test.com", "600123456");

        assertEquals(ownerId, owner.ownerId());
        assertEquals("Pepe Montana", owner.fullName());
        assertEquals("pepe@test.com", owner.email());
        assertEquals("600123456", owner.phone());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el ownerId es null")
    void ownerIdNuloLanzaExcepcion() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new OwnerDetails(null, "Pepe", "pepe@test.com", "600123456");
        });
        assertEquals("El ID del propietario no puede ser nulo.", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el nombre es null")
    void nombreNuloLanzaExcepcion() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new OwnerDetails(ownerId, null, "pepe@test.com", "600123456");
        });
        assertEquals("El nombre es obligatorio.", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el nombre esta vacio")
    void nombreVacioLanzaExcepcion() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new OwnerDetails(ownerId, "", "pepe@test.com", "600123456");
        });
        assertEquals("El nombre es obligatorio.", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el email es null")
    void emailNuloLanzaExcepcion() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new OwnerDetails(ownerId, "Pepe", null, "600123456");
        });
        assertEquals("El email es obligatorio.", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el telefono es null")
    void telefonoNuloLanzaExcepcion() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new OwnerDetails(ownerId, "Pepe", "pepe@test.com", null);
        });
        assertEquals("El numero telefonico es obligatorio.", exception.getMessage());
    }
}
