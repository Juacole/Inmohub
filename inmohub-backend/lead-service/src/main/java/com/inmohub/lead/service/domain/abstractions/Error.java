package com.inmohub.lead.service.domain.abstractions;

/**
 * Clase base para errores de dominio.
 * Encapsula un mensaje descriptivo y una excepcion opcional.
 */
public abstract class Error {
    protected final String message;
    protected final Exception exception;

    protected Error(String message) {
        this.message = message;
        this.exception = null;
    }

    protected Error(String message, Exception exception) {
        this.message = message;
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public Exception getExcepcion() {
        return exception;
    }
}