package com.inmohub.lead.service.domain.abstractions;

/**
 * Excepcion de dominio lanzada cuando se viola una regla de negocio.
 * Extiende RuntimeException para no forzar su declaracion en cada firma.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
