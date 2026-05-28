package com.inmohub.lead.service.application.usecases.errors;

import com.inmohub.lead.service.domain.abstractions.Error;

/**
 * Error lanzado cuando no se encuentra un agente en el sistema.
 */
public class AgentNotFoundError extends Error {

    public AgentNotFoundError(String message) {
        super(message, null);
    }

    public AgentNotFoundError(String message, Exception exception) {
        super(message, exception);
    }
}