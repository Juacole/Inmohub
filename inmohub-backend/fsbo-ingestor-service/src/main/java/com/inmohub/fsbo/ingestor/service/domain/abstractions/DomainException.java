package com.inmohub.fsbo.ingestor.service.domain.abstractions;

/**
 * Excepcion de dominio lanzada cuando se viola una regla de negocio.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable innerException) {
        super(message, innerException);
    }
}