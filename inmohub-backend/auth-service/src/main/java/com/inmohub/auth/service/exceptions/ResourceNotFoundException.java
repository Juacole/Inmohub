package com.inmohub.auth.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando no se encuentra una entidad solicitada en la base de datos.
 * Mapea automáticamente al código HTTP 404 "Not Found".
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Opcional
public class ResourceNotFoundException extends RuntimeException{

    /**
     * @param message Descripción del recurso que no se encontró.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
