package com.inmohub.lead.service.application.usecases.errors;

import com.inmohub.lead.service.domain.abstractions.Error;

/**
 * Error lanzado cuando los datos de entrada no cumplen con las reglas de validacion.
 */
public class ValidationError extends Error {

    public ValidationError(String message) {
        super(message, null);
    }

    public ValidationError(String message, Exception exception) {
        super(message, exception);
    }
}