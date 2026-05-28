package com.inmohub.lead.service.domain.valueobjetcs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el Value Object {@link Email}.
 * Verifica que la validacion de formato de email sea correcta
 * y que los casos limite (null, vacio, formato invalido) lancen excepcion.
 */
@DisplayName("Email Value Object")
class EmailTest {

    @Test
    @DisplayName("Debe crear un Email con formato válido")
    void crearEmailValido() {
        Email email = new Email("pepe.montana@gmail.com");
        assertEquals("pepe.montana@gmail.com", email.value());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el email es null")
    void emailNuloLanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Email(null);
        });
        assertEquals("El email no puede estar vacío.", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el email está vacío")
    void emailVacioLanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Email("");
        });
        assertEquals("El email no puede estar vacío.", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el formato del email es inválido")
    void emailFormatoInvalidoLanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Email("esto-no-es-un-email");
        });
        assertTrue(exception.getMessage().contains("Formato del email inválido"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el email está en blanco")
    void emailBlancoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Email("   ");
        });
    }
}
