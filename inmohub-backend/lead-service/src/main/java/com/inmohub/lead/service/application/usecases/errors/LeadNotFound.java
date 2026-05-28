package com.inmohub.lead.service.application.usecases.errors;

import com.inmohub.lead.service.domain.abstractions.Error;

/**
 * Error lanzado cuando no se encuentra un lead en el sistema.
 */
public class LeadNotFound extends Error {

    public LeadNotFound(String message) {
        super(message, null);
    }

    public LeadNotFound(String message, Exception exception) {
        super(message, exception);
    }
}