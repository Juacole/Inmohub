package com.inmohub.lead.service.application.usecases.errors;

import com.inmohub.lead.service.domain.abstractions.Error;

/**
 * Error lanzado cuando no se encuentra una propiedad en el sistema.
 */
public class PropertyNotFoundError extends Error {

    public PropertyNotFoundError(String message) {
        super(message, null);
    }

    public PropertyNotFoundError(String message, Exception exception) {
        super(message, exception);
    }
}