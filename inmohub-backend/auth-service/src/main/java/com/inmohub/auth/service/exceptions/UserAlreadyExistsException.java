package com.inmohub.auth.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada al intentar crear un recurso que viola una restricción de unicidad.
 * Mapea al código HTTP 409 "Conflict".
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * @param message Descripción del conflicto.
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
