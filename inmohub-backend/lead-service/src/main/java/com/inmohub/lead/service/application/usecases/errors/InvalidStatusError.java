package com.inmohub.lead.service.application.usecases.errors;

import com.inmohub.lead.service.domain.abstractions.Error;

/**
 * Error lanzado cuando se intenta asignar un estado de lead no valido.
 */
public class InvalidStatusError extends Error {

    public InvalidStatusError(String message) {
        super(message, null);
    }

    public InvalidStatusError(String message, Exception exception) {
        super(message, exception);
    }
}