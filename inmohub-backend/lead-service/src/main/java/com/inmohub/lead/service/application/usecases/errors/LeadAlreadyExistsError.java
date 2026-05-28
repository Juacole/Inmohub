package com.inmohub.lead.service.application.usecases.errors;

import com.inmohub.lead.service.domain.abstractions.Error;

/**
 * Error lanzado cuando se intenta crear un lead que ya existe en el sistema.
 */
public class LeadAlreadyExistsError extends Error {

    public LeadAlreadyExistsError(String message) {
        super(message, null);
    }

    public LeadAlreadyExistsError(String message, Exception exception) {
        super(message, exception);
    }
}