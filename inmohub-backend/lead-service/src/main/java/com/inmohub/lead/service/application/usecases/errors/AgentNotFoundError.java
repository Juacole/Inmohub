package com.inmohub.lead.service.application.usecases.errors;

import com.inmohub.lead.service.domain.abstractions.Error;

public class AgentNotFoundError extends Error {

    public AgentNotFoundError(String message) {
        super(message, null);
    }

    public AgentNotFoundError(String message, Exception exception) {
        super(message, exception);
    }
}