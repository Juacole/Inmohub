package com.inmohub.fsbo.ingestor.service.domain.abstractions;

/**
 * Contrato para errores de dominio. Define el mensaje de error y la excepcion asociada.
 */
public interface Error {
    String getMessage();
    Exception getExcepcion();
}