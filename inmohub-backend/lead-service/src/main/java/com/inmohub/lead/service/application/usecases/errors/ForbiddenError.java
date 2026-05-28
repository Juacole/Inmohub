package com.inmohub.lead.service.application.usecases.errors;

import com.inmohub.lead.service.domain.abstractions.Error;

/**
 * Error lanzado cuando un usuario no tiene permisos para realizar una accion sobre un lead.
 */
public class ForbiddenError extends Error {

    public ForbiddenError(String message) {
        super(message, null);
    }

    public ForbiddenError(String message, Exception exception) {
        super(message, exception);
    }
}